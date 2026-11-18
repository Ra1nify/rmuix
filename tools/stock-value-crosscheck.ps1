<#
.SYNOPSIS
    Ad-hoc cross-check: every `internal val X = N.dp|sp` in `*StockValues.kt` against the `coui_*`
    dimen its KDoc cites, using the Settings APK dump as the source of truth.

.DESCRIPTION
    This is **not** one of the `stock-audit.ps1` gates. It is an advisory report: the KDocs in this
    repository frequently cite a token that is *not* equal to the constant, because the constant is a
    derivation (`14dp - 2 * 5.5dp = 3dp`), a canvas-math record, or a value whose dimen is declared but
    never read. A naive equality check therefore reports mostly false positives, which is exactly why it
    does not gate anything.

    The report classifies every pair it finds:

      exact     the cited token equals the constant
      derived   the KDoc explains a derivation (minus / plus / divided / "derived" / "half")
      unread    the KDoc says the token has no reader ("never read" / "nothing reads")
      local     the KDoc says the value is a library choice ("no stock token" / "local")
      check     none of the above - the only class worth a human look

    Usage:
        powershell.exe -NoProfile -ExecutionPolicy Bypass -File tools/stock-value-crosscheck.ps1
        powershell.exe ... -Strict     # exit 1 when anything lands in `check`

.PARAMETER Root
    Repository root. Defaults to the parent of this script's directory.

.PARAMETER Apk
    Path to the decompiled Settings APK. Defaults to `C:\dump\settings_decompiled`.

.PARAMETER Strict
    Exit 1 when at least one pair is classified `check`.
#>
[CmdletBinding()]
param(
    [string]$Root = "",
    [string]$Apk = 'C:\dump\settings_decompiled',
    [switch]$Strict
)

$ErrorActionPreference = 'Stop'

if (-not $Root) {
    $Root = Split-Path -Parent (Split-Path -Parent $PSCommandPath)
}

$dimensFile = Join-Path $Apk 'resources\res\values\dimens.xml'
if (-not (Test-Path -LiteralPath $dimensFile)) {
    Write-Host "dimens.xml not found: $dimensFile" -ForegroundColor Red
    exit 2
}

$dimens = @{}
$xml = [xml](Get-Content -LiteralPath $dimensFile -Raw)
foreach ($node in $xml.resources.dimen) {
    $dimens[$node.name] = [string]$node.'#text'
}

$sources = Join-Path $Root 'rmuix-ui\src\commonMain'
$files = Get-ChildItem $sources -Recurse -Include '*StockValues.kt' -File
if (-not $files) {
    Write-Host "no *StockValues.kt under $sources" -ForegroundColor Red
    exit 2
}

$exact = 0
$derived = 0
$unread = 0
$local = 0
$check = New-Object System.Collections.ArrayList

foreach ($file in $files) {
    $lines = [System.IO.File]::ReadAllLines($file.FullName)
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -notmatch '^\s*internal val (\w+) = ([0-9.]+)\.(dp|sp)') { continue }
        $name = $matches[1]
        $value = [double]$matches[2]
        $unit = $matches[3]

        # The KDoc directly above the declaration is the only trustworthy citation.
        $doc = New-Object System.Collections.ArrayList
        for ($j = $i - 1; $j -ge 0; $j--) {
            if ($lines[$j] -match '^\s*internal val ') { break }
            if ($lines[$j] -match '^\s*\*/') { continue }
            if ($lines[$j] -match '^\s*\*') { [void]$doc.Insert(0, $lines[$j]); continue }
            if ($lines[$j] -match '^\s*$') { continue }
            break
        }
        $docText = ($doc -join ' ')
        if (-not $docText) { continue }

        # Every `coui_*` token the KDoc mentions, not just the first one: a KDoc often cites the
        # derivation's inputs ("`a` (14dp) minus the two `b` (5.5dp) sides"), and the constant may equal
        # any one of them.
        $tokenNames = @()
        foreach ($m in [regex]::Matches($docText, '(coui_[A-Za-z0-9_]+)')) {
            $candidate = $m.Groups[1].Value
            if ($dimens.ContainsKey($candidate) -and -not ($tokenNames -contains $candidate)) {
                $tokenNames += $candidate
            }
        }
        if ($tokenNames.Count -eq 0) { continue }

        $tokenName = $null
        $raw = $null
        $apkValue = 0
        $delta = [double]::MaxValue
        foreach ($candidate in $tokenNames) {
            $candidateRaw = $dimens[$candidate]
            if ($candidateRaw -notmatch '^([0-9.]+)dp$') { continue }
            $candidateValue = [double]$matches[1]
            $candidateDelta = [math]::Abs($candidateValue - $value)
            if ($candidateDelta -lt $delta) {
                $delta = $candidateDelta
                $tokenName = $candidate
                $raw = $candidateRaw
                $apkValue = $candidateValue
            }
        }
        if (-not $tokenName) { continue }

        $class = 'check'
        if ($delta -le 0.001) {
            $class = 'exact'
        } elseif ($docText -match '(derived|minus|plus|divided|\bsubtract|half|/ 2|/ 3|×|x 2)') {
            $class = 'derived'
        } elseif ($docText -match '(never read|nothing reads|no reader|never referenced|unused)') {
            $class = 'unread'
        } elseif ($docText -match '(no stock token|library choice|local choice|layout choice)') {
            $class = 'local'
        }

        switch ($class) {
            'exact' { $exact++ }
            'derived' { $derived++ }
            'unread' { $unread++ }
            'local' { $local++ }
            default {
                [void]$check.Add(('{0}:{1}  {2} = {3}{4}   {5} = {6}' -f $file.Name, ($i + 1), $name, $value, $unit, $tokenName, $raw))
            }
        }
    }
}

Write-Host ''
Write-Host ("StockValues cross-check  files={0}" -f $files.Count)
Write-Host ("  exact={0}  derived={1}  unread={2}  local={3}  check={4}" -f $exact, $derived, $unread, $local, $check.Count)

if ($check.Count -gt 0) {
    Write-Host ''
    Write-Host 'Needs a human look:' -ForegroundColor Yellow
    $check | ForEach-Object { Write-Host ('  ' + $_) }
}

Write-Host ''
if ($check.Count -eq 0) {
    Write-Host 'No unexplained mismatches.' -ForegroundColor Green
} else {
    Write-Host ('{0} pair(s) need a human look; this script is advisory and never fails the audit by default.' -f $check.Count) -ForegroundColor Yellow
}

if ($Strict -and $check.Count -gt 0) { exit 1 }
exit 0