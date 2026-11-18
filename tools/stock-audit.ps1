# Static audit helpers for the Stock ColorOS review.
#
# Read-only: nothing in this script writes to the repository. It reports counts and issue lines and
# exits non-zero when a check finds something that needs a human decision.
#
# See tools/README.md for usage, the meaning of each check and its known heuristic limits.

param(
    [ValidateSet('Tokens', 'DocsTokens', 'DocsValues', 'DocsPair', 'DocVsApk', 'DocVsCode', 'DocsDefaults', 'TestsVsCode', 'DocsRefs', 'StockVsApk', 'TokenLiveness', 'ResearchValues', 'ColorVsApk', 'DefaultsRefs', 'LintBudget', 'PlanSync', 'ShadowShape', 'UnusedParams', 'DefaultsResolve', 'ThemeMembers', 'ObjectMembers', 'ViewportUnits', 'BreakingReversal', 'TestWritePaths', 'SerializableScope', 'ResearchHeadings', 'All')]
    [string]$Check = 'All',

    [string]$Root = 'C:\R1p0\rmuix',
    [string]$Dump = 'C:\dump\settings_decompiled',
    # A second dump whose resources are also accepted as token references. Gallery carries the
    # ColorOS 17 floating navigation bar classes (`com.coui.appcompat.bottomfloating*`) that the
    # settings dump does not ship, so tokens first observed there can be cited honestly.
    [string]$ExtraDump = 'C:\dump\gallery3d_decompiled'
)

$ErrorActionPreference = 'Stop'

$ResDir = Join-Path $Dump 'resources\res'
$SrcDirs = @(
    (Join-Path $Root 'rmuix-ui\src\commonMain\kotlin'),
    (Join-Path $Root 'rmuix-preference\src\commonMain\kotlin'),
    (Join-Path $Root 'rmuix-extend\src\commonMain\kotlin'),
    (Join-Path $Root 'rmuix-glass\src\commonMain\kotlin')
) | Where-Object { Test-Path $_ }

$ValueFiles = @(
    'values\colors.xml', 'values-night\colors.xml',
    'values\dimens.xml', 'values-night\dimens.xml',
    'values\styles.xml', 'values\attrs.xml', 'values\public.xml'
)
$SubDirs = @('layout', 'drawable', 'color', 'values', 'anim', 'menu', 'xml')

# Environment prerequisites (offline ktlint jars, the %TEMP% lint harness, java) can be missing on a
# machine; those checks then skip. Count them so the final summary cannot claim a clean run when part
# of the suite never executed.
$script:skippedChecks = 0

function Get-StockFiles {
    $SrcDirs | ForEach-Object { Get-ChildItem $_ -Recurse -Include '*StockValues.kt' -File }
}

function Get-ValueFile([string]$name) {
    $path = Join-Path $ResDir $name
    if (Test-Path $path) { return $path }
    return $null
}

function Read-Text([string]$path) {
    Get-Content -Raw -Encoding UTF8 -LiteralPath $path
}

# The settings APK alone is not the whole story: `COUINavigationRailView` and friends only exist in
# gallery3d, and SystemUI carries a few more. Every name-based check therefore unions all three trees;
# `$ResDir` (settings) stays the primary source for the value-comparison checks.
$ExtraResDirs = @(
    (Join-Path $Dump '..\gallery3d_decompiled\resources\res'),
    (Join-Path $Dump '..\systemui_decompiled\resources\res')
) | ForEach-Object { [System.IO.Path]::GetFullPath($_) } | Where-Object { Test-Path $_ }

function Get-AllValueFiles([string]$name) {
    $paths = @()
    $primary = Join-Path $ResDir $name
    if (Test-Path $primary) { $paths += $primary }
    foreach ($dir in $ExtraResDirs) {
        $candidate = Join-Path $dir $name
        if (Test-Path $candidate) { $paths += $candidate }
    }
    return $paths
}

function Get-KnownNames {
    $known = New-Object 'System.Collections.Generic.HashSet[string]'
    foreach ($rel in $ValueFiles) {
        foreach ($path in (Get-AllValueFiles $rel)) {
            foreach ($m in [regex]::Matches((Read-Text $path), 'name="([^"]+)"')) { [void]$known.Add($m.Groups[1].Value) }
        }
    }
    foreach ($sub in $SubDirs) {
        foreach ($dir in (@($ResDir) + $ExtraResDirs | ForEach-Object { Join-Path $_ $sub })) {
            if (-not (Test-Path $dir)) { continue }
            Get-ChildItem $dir -File | ForEach-Object { [void]$known.Add($_.BaseName) }
        }
    }
    return $known
}

# ---------------------------------------------------------------------------------------------------
# Check 1: every coui* identifier named in a *StockValues.kt exists somewhere in the APK.
# ---------------------------------------------------------------------------------------------------
function Invoke-TokenCheck {
    $known = Get-KnownNames

    $checked = 0
    $resolved = 0
    $prefix = 0
    $issues = New-Object System.Collections.ArrayList

    foreach ($file in Get-StockFiles) {
        foreach ($m in [regex]::Matches((Read-Text $file.FullName), '\b(coui[A-Za-z0-9_]+)\b')) {
            $token = $m.Groups[1].Value
            $checked++
            if ($known.Contains($token)) { $resolved++; continue }
            if ($token.EndsWith('_')) { $prefix++; continue }
            [void]$issues.Add(("{0}: {1}" -f $file.Name, $token))
        }
    }

    Write-Host ("Tokens      checked={0} resolved={1} prefix={2} issues={3}" -f $checked, $resolved, $prefix, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 1b: every coui* name written in docs/components exists in the APK. Package-path segments and
#   the "coui_xxx_*" wildcard notation are not token claims and are skipped.
# ---------------------------------------------------------------------------------------------------
function Invoke-DocsTokensCheck {
    $known = Get-KnownNames
    $skip = @('couiswitch', 'couicompose', 'couimaterial')

    $checked = 0
    $resolved = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($dirName in @('docs\components', 'docs\zh_CN')) {
        $docsDir = Join-Path $Root $dirName
        if (-not (Test-Path $docsDir)) { continue }
        foreach ($file in Get-ChildItem $docsDir -Recurse -Include '*.md' -File) {
            # The negative lookahead drops the "coui_xxx_*" wildcard notation, which is not a token claim.
            foreach ($m in [regex]::Matches((Read-Text $file.FullName), '\b(coui[A-Za-z0-9_]+)\b(?!\*)')) {
                $token = $m.Groups[1].Value
                $checked++
                if ($known.Contains($token)) { $resolved++; continue }
                if ($token.EndsWith('_')) { continue }
                if ($skip -contains $token) { continue }
                [void]$issues.Add(("{0}: {1}" -f $file.Name, $token))
            }
        }
    }

    Write-Host ("DocsTokens  checked={0} resolved={1} issues={2}" -f $checked, $resolved, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 1c: strict numeric claims in docs, i.e. "`coui_x` = 12dp" / "`coui_x` = **12dp**", are compared
#   with the APK dimen (one level of @dimen indirection is resolved). Looser prose like
#   "2dp (`coui_x`)" or "(`coui_x`; 4dp between items)" is not matched, because the nearest number is
#   often not the token's value.
# ---------------------------------------------------------------------------------------------------
function Invoke-DocsValuesCheck {
    $dimens = @{}
    $dimensPath = Get-ValueFile 'values\dimens.xml'
    if ($dimensPath) {
        foreach ($m in [regex]::Matches((Read-Text $dimensPath), '<dimen\s+name="([^"]+)">([^<]+)</dimen>')) {
            $dimens[$m.Groups[1].Value] = $m.Groups[2].Value
        }
    }
    $resolved = @{}
    foreach ($key in $dimens.Keys) {
        $value = $dimens[$key]
        if ($value -match '^@dimen/(.+)$') {
            $ref = $Matches[1]
            if ($dimens.ContainsKey($ref)) { $resolved[$key] = $dimens[$ref] }
        } else {
            $resolved[$key] = $value
        }
    }

    $pattern = '`(coui_[A-Za-z0-9_]+)`\s*=\s*\*{0,2}([0-9.]+)(dp|sp)'
    $checked = 0
    $ok = 0
    $skipped = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($dirName in @('docs\components', 'docs\zh_CN')) {
        $docsDir = Join-Path $Root $dirName
        if (-not (Test-Path $docsDir)) { continue }
        foreach ($file in Get-ChildItem $docsDir -Recurse -Include '*.md' -File) {
            $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8
            for ($i = 0; $i -lt $lines.Count; $i++) {
                foreach ($m in [regex]::Matches($lines[$i], $pattern)) {
                    $token = $m.Groups[1].Value
                    $docValue = $m.Groups[2].Value
                    $unit = $m.Groups[3].Value
                    if (-not $resolved.ContainsKey($token)) { $skipped++; continue }
                    $apkValue = $resolved[$token]
                    if (-not $apkValue -or $apkValue -notmatch '^([0-9.]+)(dp|sp)$') { $skipped++; continue }
                    $checked++
                    $apkNumber = [double]$Matches[1]
                    $docNumber = [double]$docValue
                    if ([Math]::Abs($apkNumber - $docNumber) -lt 0.001) { $ok++ }
                    else { [void]$issues.Add(("{0}:{1} {2} doc={3}{4} apk={5}" -f $file.Name, ($i + 1), $token, $docValue, $unit, $apkValue)) }
                }
            }
        }
    }

    Write-Host ("DocsValues  checked={0} ok={1} skipped={2} issues={3}" -f $checked, $ok, $skipped, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 1d: the English and Chinese component pages must agree on the defaults table. Rows are keyed
#   by the constant name in the first cell and compared on the numeric value cell only, so prose
#   differences do not produce noise.
# ---------------------------------------------------------------------------------------------------
function Get-DocTableValues([string]$path) {
    $map = @{}
    foreach ($line in (Get-Content -LiteralPath $path -Encoding UTF8)) {
        if (-not $line.TrimStart().StartsWith('|')) { continue }
        $cells = $line.Trim().Trim('|') -split '\|' | ForEach-Object { $_.Trim().Trim('`') }
        if ($cells.Count -lt 2) { continue }
        $name = $cells[0]
        if ($name -notmatch '^[A-Z][A-Za-z0-9]*$') { continue }
        $value = $cells | Where-Object { $_ -match '^[0-9]+(\.[0-9]+)?\s*\.?\s*(dp|sp)$' } | Select-Object -First 1
        if ($value) { $map[$name] = ($value -replace '[\s.]', '') }
    }
    return $map
}

function Invoke-DocsPairCheck {
    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    $enRoot = Join-Path $Root 'docs\components'
    $zhRoot = Join-Path $Root 'docs\zh_CN\components'
    if (-not (Test-Path $enRoot) -or -not (Test-Path $zhRoot)) {
        Write-Host "DocsPair    skipped: docs/components or docs/zh_CN/components not found"
        $script:skippedChecks++
        return 0
    }
    foreach ($file in Get-ChildItem $enRoot -Recurse -Include '*.md' -File) {
        $rel = $file.FullName.Substring($enRoot.Length).TrimStart('\')
        $zhPath = Join-Path $zhRoot $rel
        if (-not (Test-Path $zhPath)) { continue }
        $en = Get-DocTableValues $file.FullName
        $zh = Get-DocTableValues $zhPath
        foreach ($key in $en.Keys) {
            if (-not $zh.ContainsKey($key)) { continue }
            $checked++
            if ($zh[$key] -ne $en[$key]) {
                [void]$issues.Add(("{0}: {1} en={2} zh={3}" -f $rel, $key, $en[$key], $zh[$key]))
            }
        }
    }
    Write-Host ("DocsPair    checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Shared: light/dark colour tables plus the attribute -> @color mapping.
# ---------------------------------------------------------------------------------------------------
function Get-ColorTables {
    $light = @{}
    $dark = @{}
    $attr = @{}

    $lightPath = Get-ValueFile 'values\colors.xml'
    if ($lightPath) {
        foreach ($m in [regex]::Matches((Read-Text $lightPath), '<color\s+name="([^"]+)">#([0-9A-Fa-f]{6,8})</color>')) {
            $hex = $m.Groups[2].Value.ToUpper(); if ($hex.Length -eq 6) { $hex = "FF" + $hex }; $light[$m.Groups[1].Value] = $hex
        }
    }
    $darkPath = Get-ValueFile 'values-night\colors.xml'
    if ($darkPath) {
        foreach ($m in [regex]::Matches((Read-Text $darkPath), '<color\s+name="([^"]+)">#([0-9A-Fa-f]{6,8})</color>')) {
            $hex = $m.Groups[2].Value.ToUpper(); if ($hex.Length -eq 6) { $hex = "FF" + $hex }; $dark[$m.Groups[1].Value] = $hex
        }
    }
    $stylesPath = Get-ValueFile 'values\styles.xml'
    $attrLight = @{}
    $attrDark = @{}
    if ($stylesPath) {
        $stylesText = Read-Text $stylesPath
        # Only the base themes are authoritative: COUIOverlay.Theme.* variants remap the same attribute
        # for accent colours and appear earlier in the file, so first-seen would resolve the wrong value.
        foreach ($m in [regex]::Matches($stylesText, '<item\s+name="(couiColor[^"]+)">@color/([^<"]+)</item>')) {
            $styleStart = $stylesText.Substring(0, $m.Index).LastIndexOf('<style name="')
            $styleName = ''
            if ($styleStart -ge 0) { $styleName = [regex]::Match($stylesText.Substring($styleStart, 200), '<style name="([^"]+)"').Groups[1].Value }
            $isBaseLight = $styleName -eq 'Theme.COUI' -or $styleName -eq 'Theme.COUI.Main'
            $isBaseDark = $styleName -eq 'Theme.COUI.Dark' -or $styleName -eq 'Theme.COUI.Main.Dark'
            if (-not ($isBaseLight -or $isBaseDark)) { continue }
            if ($isBaseDark) { if (-not $attrDark.ContainsKey($m.Groups[1].Value)) { $attrDark[$m.Groups[1].Value] = $m.Groups[2].Value } }
            else { if (-not $attrLight.ContainsKey($m.Groups[1].Value)) { $attrLight[$m.Groups[1].Value] = $m.Groups[2].Value } }
        }
    }
    foreach ($k in $attrLight.Keys) { $darkName = $null; if ($attrDark.ContainsKey($k)) { $darkName = $attrDark[$k] }; $attr[$k] = @{ Light = $attrLight[$k]; Dark = $darkName } }
    return @{ Light = $light; Dark = $dark; Attr = $attr }
}

function Resolve-StockColor($tables, [string]$token) {
    $light = $tables.Light
    $dark = $tables.Dark
    $attr = $tables.Attr

    if ($attr.ContainsKey($token)) {
        $mapping = $attr[$token]
        $lightName = [string]$mapping.Light
        $darkName = [string]$mapping.Dark
        if ($lightName -and $light.ContainsKey($lightName)) {
            $darkValue = $null
            if ($darkName -and $dark.ContainsKey($darkName)) { $darkValue = $dark[$darkName] } elseif ($darkName -and $light.ContainsKey($darkName)) { $darkValue = $light[$darkName] }
            elseif ($dark.ContainsKey($lightName)) { $darkValue = $dark[$lightName] }
            return @($light[$lightName], $darkValue)
        }
    }
    if ($light.ContainsKey($token)) {
        $darkValue = $null
        if ($dark.ContainsKey($token)) { $darkValue = $dark[$token] }
        elseif ($dark.ContainsKey($token + '_dark')) { $darkValue = $dark[$token + '_dark'] }
        return @($light[$token], $darkValue)
    }
    return $null
}

# ---------------------------------------------------------------------------------------------------
# Check 2: hex values in a comment line match the APK value of the coui* name on the same line.
# ---------------------------------------------------------------------------------------------------
function Invoke-DocVsApkCheck {
    $tables = Get-ColorTables
    $checked = 0
    $ok = 0
    $issues = New-Object System.Collections.ArrayList

    foreach ($file in Get-StockFiles) {
        foreach ($line in ((Read-Text $file.FullName) -split "`n")) {
            $nameMatch = [regex]::Match($line, '\b(couiColor[A-Za-z0-9_]+|coui_color_[A-Za-z0-9_]+)\b')
            if (-not $nameMatch.Success) { continue }
            $hexes = @([regex]::Matches($line, '#([0-9A-Fa-f]{8})') | ForEach-Object { $_.Groups[1].Value.ToUpper() })
            if ($hexes.Count -eq 0) { continue }
            $resolved = Resolve-StockColor $tables $nameMatch.Groups[1].Value
            if (-not $resolved) { continue }
            $checked++
            $expected = @($resolved | Where-Object { $_ })
            $missing = @($hexes | Where-Object { $expected -notcontains $_ })
            if ($missing.Count -eq 0) { $ok++ }
            else { [void]$issues.Add(("{0}: {1} docs={2} apk={3}" -f $file.Name, $nameMatch.Groups[1].Value, ($hexes -join ','), ($expected -join ','))) }
        }
    }

    Write-Host ("DocVsApk    checked={0} ok={1} issues={2}" -f $checked, $ok, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 3: hex values in the nearest comment above a function match its Color(0x...) literals.
#   Known false positives: multi-value comment blocks and the look-back window drifting into the
#   previous function's comment. Confirm by hand before changing code.
# ---------------------------------------------------------------------------------------------------
function Invoke-DocVsCodeCheck {
    $checked = 0
    $ok = 0
    $issues = New-Object System.Collections.ArrayList

    foreach ($file in Get-StockFiles) {
        $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $codeHexes = @([regex]::Matches($lines[$i], 'Color\(0x([0-9A-Fa-f]{8})\)') | ForEach-Object { $_.Groups[1].Value.ToUpper() })
            if ($codeHexes.Count -lt 2) { continue }

            $docHexes = $null
            for ($j = $i - 1; $j -ge 0 -and $j -gt $i - 6; $j--) {
                # A declaration line means the comment above it belongs to a different symbol.
                if ($lines[$j] -match '^\s*(?:internal |private |public |@Composable |@Deprecated)?(?:fun|val|var|object|class) ') { break }
                $candidate = @([regex]::Matches($lines[$j], '#([0-9A-Fa-f]{8})') | ForEach-Object { $_.Groups[1].Value.ToUpper() })
                if ($candidate.Count -ge 2) { $docHexes = $candidate; break }
                if ($lines[$j].Trim().StartsWith('}')) { break }
            }
            if (-not $docHexes) { continue }

            $checked++
            $missing = @($docHexes | Where-Object { $codeHexes -notcontains $_ })
            $extra = @($codeHexes | Where-Object { $docHexes -notcontains $_ })
            if ($missing.Count -eq 0 -and $extra.Count -eq 0) { $ok++ }
            else { [void]$issues.Add(("{0}:{1}: doc={2} code={3}" -f $file.Name, ($i + 1), ($docHexes -join ','), ($codeHexes -join ','))) }
        }
    }

    Write-Host ("DocVsCode   checked={0} ok={1} issues={2}" -f $checked, $ok, $issues.Count)
    $issues | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 4: documented default values in docs/components match the literal or one-level-aliased
#   initialiser on the matching XxxDefaults object.
# ---------------------------------------------------------------------------------------------------
function Invoke-DocsDefaultsCheck {
    $symbols = @{}
    $resolved = @{}

    foreach ($file in Get-ChildItem $SrcDirs -Recurse -Include '*.kt' -File) {
        $text = Read-Text $file.FullName
        foreach ($m in [regex]::Matches($text, '(?m)^\s*(?:internal\s+|private\s+)?val\s+(\w+)\s*(?::\s*[\w<>?]+\s*)?=\s*([0-9.]+)\s*\.(dp|sp)\b')) {
            if (-not $symbols.ContainsKey($m.Groups[1].Value)) { $symbols[$m.Groups[1].Value] = "$($m.Groups[2].Value).$($m.Groups[3].Value)" }
        }
    }

    foreach ($file in Get-ChildItem $SrcDirs -Recurse -Include '*.kt' -File) {
        $text = Read-Text $file.FullName
        foreach ($obj in [regex]::Matches($text, 'object\s+(\w+Defaults)\s*\{')) {
            $name = $obj.Groups[1].Value
            $depth = 0
            $end = $obj.Index
            for ($i = $obj.Index + $obj.Length - 1; $i -lt $text.Length; $i++) {
                if ($text[$i] -eq '{') { $depth++ } elseif ($text[$i] -eq '}') { $depth--; if ($depth -eq 0) { $end = $i; break } }
            }
            $body = $text.Substring($obj.Index, $end - $obj.Index)
            foreach ($m in [regex]::Matches($body, '(?m)^\s*val\s+(\w+)\s*(?::\s*[\w<>?]+\s*)?=\s*([0-9.]+)\s*\.(dp|sp)\b')) {
                $resolved["$name|$($m.Groups[1].Value)"] = "$($m.Groups[2].Value).$($m.Groups[3].Value)"
            }
            foreach ($m in [regex]::Matches($body, '(?m)^\s*val\s+(\w+)\s*(?::\s*[\w<>?]+\s*)?=\s*([\w.]+)\s*$')) {
                $rhs = $m.Groups[2].Value
                if ($symbols.ContainsKey($rhs)) { $resolved["$name|$($m.Groups[1].Value)"] = $symbols[$rhs] }
            }
        }
    }

    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    $docsRoot = Join-Path $Root 'docs\components'
    if (Test-Path $docsRoot) {
        foreach ($file in Get-ChildItem $docsRoot -Recurse -Include '*.md' -File) {
            $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8
            $scope = $null
            for ($i = 0; $i -lt $lines.Count; $i++) {
                $line = $lines[$i]
                $scopeMatch = [regex]::Match($line, '(\w+Defaults)')
                if ($line.TrimStart().StartsWith('#')) {
                    $scope = if ($scopeMatch.Success) { $scopeMatch.Groups[1].Value } else { $null }
                    continue
                }
                if (-not $line.TrimStart().StartsWith('|')) { continue }
                if ($scopeMatch.Success) { $scope = $scopeMatch.Groups[1].Value }

                $cells = $line.Trim().Trim('|') -split '\|' | ForEach-Object { $_.Trim().Trim('`') }
                if ($cells.Count -lt 3) { continue }
                $valueCell = $cells | Where-Object { $_ -match '^[0-9.]+\.(dp|sp)$' } | Select-Object -First 1
                if (-not $valueCell -or -not $scope) { continue }

                $key = "$scope|$($cells[0])"
                if ($resolved.ContainsKey($key)) {
                    $checked++
                    if ($resolved[$key] -ne $valueCell) {
                        [void]$issues.Add(("{0}:{1}: {2} docs={3} code={4}" -f $file.Name, ($i + 1), $key, $valueCell, $resolved[$key]))
                    }
                }
            }
        }
    }

    Write-Host ("DocsDefaults checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 8: unit tests that assert a literal against an `XxxDefaults.Member` must still match the code.
#   Only `<Number>.dp` / `<Number>.sp` literals are compared; members whose initialiser is not a plain
#   literal or a one-level alias are skipped.
# ---------------------------------------------------------------------------------------------------
function Invoke-TestsVsCodeCheck {
    $globals = @{}
    $members = @{}
    $sourceFiles = Get-ChildItem $SrcDirs -Recurse -Include '*.kt' -File

    # Pass 1: every top-level dp/sp literal, so aliases can point at constants declared in another file.
    foreach ($file in $sourceFiles) {
        foreach ($m in [regex]::Matches((Read-Text $file.FullName), '(?m)^\s*(?:internal\s+|public\s+|private\s+)?val\s+(\w+)(?:\s*:\s*[\w<>?]+)?\s*=\s*([0-9.]+)\.(dp|sp)')) {
            if (-not $globals.ContainsKey($m.Groups[1].Value)) {
                $globals[$m.Groups[1].Value] = "$($m.Groups[2].Value).$($m.Groups[3].Value)"
            }
        }
    }

    # Pass 2: resolve the members of every XxxDefaults object.
    foreach ($file in $sourceFiles) {
        $text = Read-Text $file.FullName
        foreach ($obj in [regex]::Matches($text, 'object\s+(\w+Defaults)\s*\{')) {
            $name = $obj.Groups[1].Value
            $depth = 0
            $end = $obj.Index
            for ($i = $obj.Index + $obj.Length - 1; $i -lt $text.Length; $i++) {
                if ($text[$i] -eq '{') { $depth++ } elseif ($text[$i] -eq '}') { $depth--; if ($depth -eq 0) { $end = $i; break } }
            }
            $body = $text.Substring($obj.Index, $end - $obj.Index)
            foreach ($m in [regex]::Matches($body, '(?m)^\s*val\s+(\w+)(?:\s*:\s*[\w<>?]+)?\s*=\s*([0-9.]+)\.(dp|sp)')) {
                $members["$name|$($m.Groups[1].Value)"] = "$($m.Groups[2].Value).$($m.Groups[3].Value)"
            }
            foreach ($m in [regex]::Matches($body, '(?m)^\s*val\s+(\w+)(?:\s*:\s*[\w<>?]+)?\s*=\s*(\w+)\s*$')) {
                $rhs = $m.Groups[2].Value
                if ($globals.ContainsKey($rhs)) { $members["$name|$($m.Groups[1].Value)"] = $globals[$rhs] }
            }

            # Third pass: members defined by arithmetic over other members of the same object, e.g.
            # al Travel: Dp = Width - ThumbPadding * 2 - ThumbSize. Substituted values are checked to
            # be plain numbers before evaluation, so nothing but digits and operators reaches the evaluator.
            $arithmetic = New-Object System.Collections.ArrayList
            foreach ($m in [regex]::Matches($body, '(?m)^\s*val\s+(\w+)(?:\s*:\s*[\w<>?]+)?\s*=\s*([A-Za-z0-9_+\-*/(). ]+)\s*$')) {
                [void]$arithmetic.Add(@($m.Groups[1].Value, $m.Groups[2].Value))
            }
            for ($round = 0; $round -lt 5; $round++) {
                $progress = $false
                foreach ($entry in $arithmetic) {
                    $memberName = $entry[0]
                    if ($members.ContainsKey("$name|$memberName")) { continue }
                    $expression = $entry[1]
                    $unit = $null
                    $resolvedExpression = $expression
                    $allResolved = $true
                    foreach ($word in [regex]::Matches($expression, '[A-Za-z_]\w*')) {
                        $sibling = "$name|$($word.Value)"
                        if ($members.ContainsKey($sibling)) {
                            $value = $members[$sibling]
                            $parts = $value -split '\.'
                            if ($unit -eq $null) { $unit = $parts[1] } elseif ($unit -ne $parts[1]) { $allResolved = $false; break }
                            $resolvedExpression = $resolvedExpression -replace "(?<![A-Za-z0-9_])$([regex]::Escape($word.Value))(?![A-Za-z0-9_])", $parts[0]
                            continue
                        }
                        if ($globals.ContainsKey($word.Value)) {
                            $parts = $globals[$word.Value] -split '\.'
                            if ($unit -eq $null) { $unit = $parts[1] } elseif ($unit -ne $parts[1]) { $allResolved = $false; break }
                            $resolvedExpression = $resolvedExpression -replace "(?<![A-Za-z0-9_])$([regex]::Escape($word.Value))(?![A-Za-z0-9_])", $parts[0]
                            continue
                        }
                        $allResolved = $false
                        break
                    }
                    if (-not $allResolved -or -not $unit) { continue }
                    if ($resolvedExpression -notmatch '^[0-9+\-*/(). ]+$') { continue }
                    try { $number = [double](Invoke-Expression $resolvedExpression) } catch { continue }
                    $members["$name|$memberName"] = "$number.$unit"
                    $progress = $true
                }
                if (-not $progress) { break }
            }
        }
    }

    $checked = 0
    $skipped = 0
    $issues = New-Object System.Collections.ArrayList
    $testRoots = @(
        (Join-Path $Root 'rmuix-ui\src\commonTest'),
        (Join-Path $Root 'rmuix-preference\src\commonTest'),
        (Join-Path $Root 'rmuix-extend\src\commonTest'),
        (Join-Path $Root 'rmuix-glass\src\commonTest')
    ) | Where-Object { Test-Path $_ }

    foreach ($root in $testRoots) {
        foreach ($file in Get-ChildItem $root -Recurse -Include '*.kt' -File) {
            $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8
            for ($i = 0; $i -lt $lines.Count; $i++) {
                $m = [regex]::Match($lines[$i], 'assertEquals\(\s*([0-9.]+)\.(dp|sp)\s*,\s*([A-Za-z]\w*)\.(\w+)\s*\)')
                if (-not $m.Success) { continue }
                $literal = "$($m.Groups[1].Value).$($m.Groups[2].Value)"
                $key = "$($m.Groups[3].Value)|$($m.Groups[4].Value)"
                if (-not $members.ContainsKey($key)) { $skipped++; continue }
                $checked++
                if ($members[$key] -ne $literal) {
                    [void]$issues.Add(("{0}:{1} {2} expects {3} but the code resolves to {4}" -f $file.Name, ($i + 1), $key, $literal, $members[$key]))
                }
            }
        }
    }

    Write-Host ("TestsVsCode checked={0} skipped={1} issues={2}" -f $checked, $skipped, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 9: every `XxxDefaults.Member` written in the component docs must exist on that object.
#   Nested enum / class / interface declarations count as members, and names are matched
#   case-sensitively -- a docs table naming a type that was moved out of the object is the failure
#   mode this check exists for.
# ---------------------------------------------------------------------------------------------------
function Invoke-DocsRefsCheck {
    $objects = @{}
    foreach ($file in Get-ChildItem $SrcDirs -Recurse -Include '*.kt' -File) {
        $text = Read-Text $file.FullName
        foreach ($obj in [regex]::Matches($text, 'object\s+(\w+Defaults)\s*\{')) {
            $name = $obj.Groups[1].Value
            if ($objects.ContainsKey($name)) { continue }
            $depth = 0
            $end = $obj.Index + $obj.Length
            for ($i = $obj.Index + $obj.Length - 1; $i -lt $text.Length; $i++) {
                if ($text[$i] -eq '{') { $depth++ } elseif ($text[$i] -eq '}') { $depth--; if ($depth -eq 0) { $end = $i; break } }
            }
            $body = $text.Substring($obj.Index, [Math]::Max(0, $end - $obj.Index))
            $members = New-Object 'System.Collections.Generic.HashSet[string]'
            foreach ($m in [regex]::Matches($body, '(?:^|\n)\s*(?:@\w+(?:\([^)]*\))?\s*)*(?:internal |public |private )?(?:const )?(?:val|var|fun|class|enum class|interface|object)\s+(?:<[^>\r\n]+>\s*)?(\w+)')) {
                [void]$members.Add($m.Groups[1].Value)
            }
            $objects[$name] = $members
        }
    }

    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($dirName in @('docs\components', 'docs\zh_CN')) {
        $docsDir = Join-Path $Root $dirName
        if (-not (Test-Path $docsDir)) { continue }
        foreach ($file in Get-ChildItem $docsDir -Recurse -Include '*.md' -File) {
            foreach ($m in [regex]::Matches((Read-Text $file.FullName), '\b([A-Z]\w*Defaults)\.(\w+)')) {
                $objName = $m.Groups[1].Value
                $member = $m.Groups[2].Value
                if (-not $objects.ContainsKey($objName)) { continue }
                $checked++
                if (-not $objects[$objName].Contains($member)) {
                    [void]$issues.Add(("{0}: {1}.{2}" -f $file.Name, $objName, $member))
                }
            }
        }
    }

    Write-Host ("DocsRefs    checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 10: a *_StockValues constant whose KDoc cites exactly one `coui_*` dimen must match that dimen.
#   Skipped on purpose: docs naming several tokens (they use " / " or "、"), values the doc describes as
#   derived, and tokens whose APK value is not a plain number.
# ---------------------------------------------------------------------------------------------------
function Invoke-StockVsApkCheck {
    $dimens = @{}
    $dimensPath = Get-ValueFile 'values\dimens.xml'
    if ($dimensPath) {
        foreach ($m in [regex]::Matches((Read-Text $dimensPath), '<dimen\s+name="([^"]+)">([^<]+)</dimen>')) {
            $dimens[$m.Groups[1].Value] = $m.Groups[2].Value
        }
    }

    $exact = 0
    $unitOnly = 0
    $derived = 0
    $multiCited = 0
    $issues = New-Object System.Collections.ArrayList

    foreach ($file in Get-StockFiles) {
        $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $valueMatch = [regex]::Match($lines[$i], '^internal val (\w+) = ([0-9.]+)\.(dp|sp)')
            if (-not $valueMatch.Success) { continue }
            $name = $valueMatch.Groups[1].Value
            $codeNumber = [double]$valueMatch.Groups[2].Value
            $codeUnit = $valueMatch.Groups[3].Value

            $doc = ''
            for ($j = $i - 1; $j -ge 0 -and $j -gt $i - 12; $j--) {
                if ($lines[$j].Trim() -eq '') { break }
                $doc = $lines[$j] + ' ' + $doc
            }
            # Accept any backticked name that is an actual dimen in the dump, not just `coui_*`:
            # COUI ships many unprefixed dimens (`stepper_button_size`, `tool_tips_max_width`,
            # `detail_floating_text_margin`), and skipping them left those values unguarded.
            $tokens =
                @(
                    [regex]::Matches($doc, '`([A-Za-z][A-Za-z0-9_]*)`') |
                        ForEach-Object { $_.Groups[1].Value } |
                        Where-Object { $dimens.ContainsKey($_) } |
                        Sort-Object -Unique
                )
            if ($tokens.Count -ne 1) { continue }
            if ($doc -match ' / ' -or $doc.Contains([char]0x3001)) { $multiCited++; continue }

            $token = $tokens[0]
            if (-not $dimens.ContainsKey($token)) { continue }
            $apkMatch = [regex]::Match($dimens[$token], '^([0-9.]+)(dp|sp)$')
            if (-not $apkMatch.Success) { continue }
            $apkNumber = [double]$apkMatch.Groups[1].Value
            $apkUnit = $apkMatch.Groups[2].Value

            if ([Math]::Abs($apkNumber - $codeNumber) -lt 0.001 -and $apkUnit -eq $codeUnit) { $exact++; continue }
            if ([Math]::Abs($apkNumber - $codeNumber) -lt 0.001) { $unitOnly++; continue }
            if ($doc -match 'derived|half|minus|split|per |- 2') { $derived++; continue }
            [void]$issues.Add(("{0}:{1} {2}={3}.{4} but {5}={6}" -f $file.Name, ($i + 1), $name, $codeNumber, $codeUnit, $token, $dimens[$token]))
        }
    }

    Write-Host ("StockVsApk  exact={0} unitOnly={1} derived={2} multiCited={3} issues={4}" -f $exact, $unitOnly, $derived, $multiCited, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------
# Check 11: every `coui_*` token cited in a `*StockValues.kt` KDoc must actually be referenced by the
#   decompiled app somewhere (`R.<type>.<name>` in java, or `@type/name` / `?attr/name` in xml).
#   A token that exists but is never read is a dead resource: citing it as the source of a value is a
#   false attribution. `` lists the ones already documented as dead.
# ---------------------------------------------------------------------------------------------------
function Invoke-TokenLivenessCheck {
    $allowList = @(
        'coui_floating_button_fab_mini_size',
        'coui_navigation_item_gap',
        'coui_popup_list_window_content_radius',
        'coui_search_view_close_button_selector',
        'coui_search_view_text_hint_size',
        'coui_search_view_text_size',
        'coui_appbar_title_collapsed_with_subtitle_text_size',
        # Declared in COUIToolbar's R/dimens but the class never reads it; no offset is applied.
        'coui_toolbar_subtitle_offset_top',
        'coui_navigation_item_height',
        'coui_search_view_margin_left',
        'coui_search_view_margin_right',
        'coui_dialog_button_height',
        'coui_dialog_builder_button_content_divider_margin_horizontal',
        'coui_animation_time_flashing',
        'coui_animation_time_loop',
        'coui_animation_time_moment',
        'coui_animation_time_move',
        'coui_animation_time_move_fast',
        'coui_animation_time_move_slow',
        'coui_animation_time_move_veryfast',
        'coui_animation_time_move_veryslow',
        'coui_snack_bar_max_width',
        'coui_popup_list_window_item_title_margin_left',
        'coui_slide_selector_item_height',
        'coui_bottom_floating_navigation_bar_menu_to_screen_navi_distance',
        'coui_snack_bar_radius',
        'coui_snack_bar_background_radius',
        # Defined in the tips library's dimens.xml but never read by code or layout in this dump;
        # the tips padding is applied through the container's own getPadding* instead.
        'coui_toptips_view_padding',
        'coui_toptips_view_padding_multi_line',
        'coui_popup_list_window_item_checkbox_margin_end',
        'coui_breadcrumb',
        'coui_snack_bar_radius_single_line',
        'coui_seekbar_progress_pressed_padding_horizontal',
        'coui_seekbar_view_max_width',
        'coui_vertical_seekbar_view_max_height',
        'coui_tool_tips_delete_icon_size',
        'coui_tool_tips_delete_icon_padding',
        'coui_icon_loading',
        'coui_popup_list_window_mask_margin_vertical',
        # Defined in the touchsearch library's dimens.xml but never read through R.dimen or a layout in
        # this dump (`COUITouchSearchView` takes its height from the layout and computes the dot levels
        # at runtime); the constants still come straight from these definitions.
        'coui_touchsearch_default_height',
        'coui_touchsearch_limit1_height',
        'coui_touchsearch_min_height',
        'coui_touchsearch_popupwin_margin',
        # Declared in coui_touchsearchview colors; the pressed/unpressed selector is state-based, so the
        # standalone unpressed value is never referenced by a selector or code read.
        'coui_touchsearchview_key_text_color_unpressed'
    )

    $references = New-Object 'System.Collections.Generic.HashSet[string]'
    $javaRe = [regex]'R\.(?:dimen|color|integer|drawable|style|styleable|attr|layout|array|anim|font|string)\.([A-Za-z_][A-Za-z0-9_]*)'
    # `com.google.android.material` carries COUI's own app-bar classes (e.g.
    # `COUIDividerAppBarLayout`), so its `R.*` reads count as references too.
    foreach ($dirName in @('com\coui', 'com\oplus', 'com\android\settings', 'com\support', 'com\google\android\material')) {
        $dirPath = Join-Path $Dump ('sources\' + $dirName)
        if (-not (Test-Path $dirPath)) { continue }
        foreach ($file in Get-ChildItem $dirPath -Recurse -Include '*.java' -File) {
            foreach ($m in $javaRe.Matches([System.IO.File]::ReadAllText($file.FullName))) { [void]$references.Add($m.Groups[1].Value) }
        }
    }
    $xmlRe = [regex]'@(?:dimen|color|integer|drawable|style|layout|array|anim|font)/(?:[A-Za-z0-9_.]+/)?([A-Za-z_][A-Za-z0-9_]*)|@boolean/([A-Za-z_][A-Za-z0-9_]*)|[?]attr/([A-Za-z_][A-Za-z0-9_]*)'
    if ($ExtraDump -and (Test-Path $ExtraDump)) {
        $extraSources = Join-Path $ExtraDump 'sources\com\coui'
        if (Test-Path $extraSources) {
            foreach ($file in Get-ChildItem $extraSources -Recurse -Include '*.java' -File) {
                foreach ($m in $javaRe.Matches([System.IO.File]::ReadAllText($file.FullName))) { [void]$references.Add($m.Groups[1].Value) }
            }
        }
        $extraRes = Join-Path $ExtraDump 'resources\res'
        if (Test-Path $extraRes) {
            foreach ($file in Get-ChildItem $extraRes -Recurse -Include '*.xml' -File | Where-Object { $_.Name -ne 'public.xml' }) {
                foreach ($m in $xmlRe.Matches([System.IO.File]::ReadAllText($file.FullName))) {
                    foreach ($group in 1..3) { if ($m.Groups[$group].Value) { [void]$references.Add($m.Groups[$group].Value) } }
                }
            }
        }
    }
    foreach ($file in Get-ChildItem $ResDir -Recurse -Include '*.xml' -File | Where-Object { $_.Name -ne 'public.xml' }) {
        foreach ($m in $xmlRe.Matches([System.IO.File]::ReadAllText($file.FullName))) {
            foreach ($group in 1..3) { if ($m.Groups[$group].Value) { [void]$references.Add($m.Groups[$group].Value) } }
        }
    }

    $cited = @{}
    foreach ($file in Get-ChildItem $SrcDirs -Recurse -Include '*.kt' -File) {
        $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8
        for ($i = 0; $i -lt $lines.Count; $i++) {
            # Only comments: a token named in prose is an attribution claim. Trailing "_" marks a
            # deliberately truncated family prefix (e.g. `coui_appbar_title_expanded_`), not a token.
            if ($lines[$i] -notmatch '^\s*(//|\*|/\*)') { continue }
            foreach ($m in [regex]::Matches($lines[$i], '`?(coui_[A-Za-z0-9_]+)`?')) {
                $token = $m.Groups[1].Value
                if ($token.EndsWith('_')) { continue }
                if (-not $cited.ContainsKey($token)) { $cited[$token] = New-Object System.Collections.ArrayList }
                [void]$cited[$token].Add(("{0}:{1}" -f $file.Name, ($i + 1)))
            }
        }
    }
    $issues = New-Object System.Collections.ArrayList
    foreach ($token in ($cited.Keys | Sort-Object)) {
        if ($references.Contains($token)) { continue }
        # A cited name may be a truncation of a real resource (`coui_btn_check_off/on_normal`) or a
        # wildcard family (`coui_breadcrumb*`). Both are legitimate prose, not token claims.
        $isPrefixOfReal = $false
        foreach ($known in $references) {
            if ($known.StartsWith($token) -and $known.Length -gt $token.Length) { $isPrefixOfReal = $true; break }
        }
        if ($isPrefixOfReal) { continue }
        if ($allowList -contains $token) { continue }
        [void]$issues.Add(("{0} is cited but no java/xml reference exists ({1})" -f $token, ($cited[$token] -join ', ')))
    }

    Write-Host ("TokenLiveness cited={0} referenced={1} allowListed={2} issues={3}" -f $cited.Count, $references.Count, $allowList.Count, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------
# Check 12: table rows in `docs/dev` that pair a `coui_*` dimen with a `12dp` / `12sp` cell
#   must agree with the APK value. Rows that talk about a missing or unconfirmed resource are skipped.
# ---------------------------------------------------------------------------------------------------
function Invoke-ResearchValuesCheck {
    $dimens = @{}

    $dimensPath = Get-ValueFile 'values\dimens.xml'
    if ($dimensPath) {
        foreach ($m in [regex]::Matches((Read-Text $dimensPath), '<dimen\s+name="([^"]+)">([^<]+)</dimen>')) { $dimens[$m.Groups[1].Value] = $m.Groups[2].Value }
    }

    $researchRoot = Join-Path $Root 'docs\dev'
    $checked = 0
    $ok = 0
    $issues = New-Object System.Collections.ArrayList
    if (Test-Path $researchRoot) {
        foreach ($file in Get-ChildItem $researchRoot -Filter '*.md' -File) {
            $lineNumber = 0
            foreach ($line in ((Read-Text $file.FullName) -split "`n")) {

                $lineNumber++
                if (-not $line.TrimStart().StartsWith('|')) { continue }
                if ($line -match '不存在|未找到|无此|不确定|猜测') { continue }
                $cells = @($line.Trim().Trim('|') -split '\|' | ForEach-Object { $_.Trim() })
                $tokenCells = @($cells | Where-Object { $_ -match 'coui_[A-Za-z0-9_]+' })
                if ($tokenCells.Count -eq 0) { continue }
                $token = [regex]::Match($tokenCells[0], '(coui_[A-Za-z0-9_]+)').Groups[1].Value
                if (-not $dimens.ContainsKey($token)) { continue }
                $valueCells = @($cells | Where-Object { $clean = $_.Trim().Trim('`'); $clean -match '^[0-9]+(\.[0-9]+)?(dp|sp)$' })
                if ($valueCells.Count -eq 0) { continue }
                $docValue = [regex]::Match([string]$valueCells[0], '([0-9]+(\.[0-9]+)?)\s*(dp|sp)')
                if (-not $docValue.Success) { continue }
                $apkRaw = [string]$dimens[$token]
                if ($apkRaw -match '^@dimen/(.+)$') { $apkRaw = [string]$dimens[$matches[1]] }
                $apkValue = [regex]::Match($apkRaw, '^([0-9.]+)(dp|sp)$')
                if (-not $apkValue.Success) { continue }
                $checked++
                $same =
                    [Math]::Abs([double]$apkValue.Groups[1].Value - [double]$docValue.Groups[1].Value) -lt 0.001 -and
                    $apkValue.Groups[2].Value -eq $docValue.Groups[3].Value
                if ($same) { $ok++ }
                else {
                    [void]$issues.Add(("{0}:{1} {2} doc={3}{4} apk={5}" -f $file.Name, $lineNumber, $token, $docValue.Groups[1].Value, $docValue.Groups[3].Value, $apkRaw))
                }
            }
        }
    }

    Write-Host ("ResearchValues checked={0} ok={1} issues={2}" -f $checked, $ok, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------
# Check 13: a Color(0x…) literal in a *_StockValues.kt function whose KDoc cites one ``couiColor*`` /
#   ``coui_color_*`` token must be one of that token's APK colours (light or dark, per theme).
# ---------------------------------------------------------------------------------------------------
function Invoke-ColorVsApkCheck {
    $tables = Get-ColorTables
    $checked = 0
    $ok = 0
    $skipped = 0
    $issues = New-Object System.Collections.ArrayList

    foreach ($file in Get-StockFiles) {
        $lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $codeHexes = @([regex]::Matches($lines[$i], 'Color\(0x([0-9A-Fa-f]{8})\)') | ForEach-Object { $_.Groups[1].Value.ToUpper() })
            if ($codeHexes.Count -eq 0) { continue }
            $doc = ''
            for ($j = $i - 1; $j -ge 0 -and $j -gt $i - 10; $j--) {
                if ($lines[$j].Trim() -eq '') { break }
                $doc = $lines[$j] + ' ' + $doc
            }
            $tokenMatch = [regex]::Match($doc, '(couiColor[A-Za-z0-9_]+|coui_color_[A-Za-z0-9_]+)')
            if (-not $tokenMatch.Success) { $skipped++; continue }
            $token = $tokenMatch.Groups[1].Value
            $expected = @(Resolve-StockColor $tables $token | Where-Object { $_ })
            if ($expected.Count -eq 0) { $skipped++; continue }
            $checked++
            $missing = @($codeHexes | Where-Object { $expected -notcontains $_ })
            if ($missing.Count -eq 0) { $ok++ }
            else { [void]$issues.Add(("{0}:{1} {2} code={3} apk={4}" -f $file.Name, ($i + 1), $token, ($codeHexes -join ','), ($expected -join ','))) }
        }
    }

    Write-Host ("ColorVsApk  checked={0} ok={1} skipped={2} issues={3}" -f $checked, $ok, $skipped, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

# ---------------------------------------------------------------------------------------------------
# Check 14: every `XxxDefaults.Member` reference in kotlin source must resolve to a member declared in
#   that object (comments excluded, objects from other libraries skipped). Catches stale renames that
#   ktlint cannot see, because it has no type resolution.
# ---------------------------------------------------------------------------------------------------
function Invoke-DefaultsRefsCheck {
    $declarationPattern = '(?m)^\s*(?:(?:public|internal|private|const|override|open|actual)\s+)*(?:val|var|fun|class|enum class|data class|interface|object)\s+(?:<[^>]+>\s+)?(\w+)'
    $members = @{}
    $sourceFiles = Get-ChildItem $SrcDirs -Recurse -Include '*.kt' -File

    foreach ($file in $sourceFiles) {
        $text = Read-Text $file.FullName
        foreach ($obj in [regex]::Matches($text, 'object\s+(\w+Defaults)\s*\{')) {
            $name = $obj.Groups[1].Value
            $depth = 0
            $end = $obj.Index
            for ($i = $obj.Index + $obj.Length - 1; $i -lt $text.Length; $i++) {
                if ($text[$i] -eq '{') { $depth++ } elseif ($text[$i] -eq '}') { $depth--; if ($depth -eq 0) { $end = $i; break } }
            }
            $body = $text.Substring($obj.Index, $end - $obj.Index)
            if (-not $members.ContainsKey($name)) { $members[$name] = New-Object 'System.Collections.Generic.HashSet[string]' }
            foreach ($m in [regex]::Matches($body, $declarationPattern)) { [void]$members[$name].Add($m.Groups[1].Value) }
        }
    }

    $checked = 0
    $skipped = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($file in $sourceFiles) {
        $text = Read-Text $file.FullName
        $noBlockComments = [regex]::Replace($text, '(?s)/\*.*?\*/', '')
        $code = [regex]::Replace($noBlockComments, '(?m)//.*$', '')
        foreach ($m in [regex]::Matches($code, '\b([A-Z]\w*Defaults)\.(\w+)')) {
            $objName = $m.Groups[1].Value
            if (-not $members.ContainsKey($objName)) { $skipped++; continue }
            $checked++
            if (-not $members[$objName].Contains($m.Groups[2].Value)) {
                [void]$issues.Add(("{0}: {1}.{2} is not declared in that object" -f $file.Name, $objName, $m.Groups[2].Value))
            }
        }
    }

    Write-Host ("DefaultsRefs checked={0} skipped={1} issues={2}" -f $checked, $skipped, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}
if (-not (Test-Path $ResDir)) { throw "APK resource directory not found: $ResDir" }
if (-not $SrcDirs) { throw "No rmuix source directories found under $Root" }

$total = 0
function Invoke-LintBudgetCheck {
    # The audit checks in this file are text heuristics over docs and sources; this one is different - it
    # runs the same offline ktlint the review rounds use, over *every* library Kotlin file, and compares
    # the per-rule violation counts against a recorded baseline.
    #
    # Why it exists: rounds 1-135 reported "lint clean" for the files they touched, which was true but was
    # read as a statement about the whole library. A census in round 135 found 712 pre-existing violations
    # across 154 files; the library was then reformatted in one deliberate pass, leaving only the two
    # Census captured 2026-11-12 over the 155 library Kotlin files. Update a number only after a round
    # actually removes those violations; the check fails when any rule exceeds its recorded count.
    $baseline = [ordered]@{
        # 2026-11-12: the whole library was reformatted (155 files) and the six icon files were moved to a
        # `by lazy` backing property. Only these two remain, both deliberate API decisions - see the
        # SlideView row in docs/dev/stock-coloros-review-index.md.
        'compose:param-order-check' = 2
    }

    $baselineTotal = 2

    $cache = 'C:\Users\aka\.gradle\caches\modules-2\files-2.1'
    $wanted = @(
        'com.pinterest.ktlint\ktlint-rule-engine-core\1.8.0',
        'com.pinterest.ktlint\ktlint-rule-engine\1.8.0',
        'com.pinterest.ktlint\ktlint-ruleset-standard\1.8.0',
        'com.pinterest.ktlint\ktlint-logger\1.8.0',
        'com.pinterest.ktlint\ktlint-cli-ruleset-core\1.8.0',
        'io.nlopez.compose.rules\ktlint\0.6.8',
        'io.nlopez.compose.rules\common-ktlint\0.6.8',
        'org.ec4j.core\ec4j-core\1.1.1',
        'io.github.oshai\kotlin-logging-jvm\7.0.13',
        'org.slf4j\slf4j-api\2.0.7',
        'org.jetbrains.kotlin\kotlin-compiler-embeddable\2.2.21',
        'org.jetbrains.kotlin\kotlin-stdlib\2.2.21',
        'org.jetbrains.kotlin\kotlin-script-runtime\2.2.21',
        'org.jetbrains.intellij.deps\trove4j\1.0.20200330',
        'org.jetbrains\annotations\13.0',
        'org.jetbrains.kotlinx\kotlinx-coroutines-core-jvm\1.10.2'
    )
    foreach ($item in $wanted) {
        if (-not (Test-Path (Join-Path $cache $item))) {
            Write-Host "LintBudget skipped=missing-ktlint-jar issues=0"
            $script:skippedChecks++
            Write-Host "  $item is not in the Gradle cache; the budget check needs the offline ktlint toolchain."
            return 0
        }
    }
    $java = 'C:\Users\aka\.gradle\jdks\eclipse_adoptium-21-amd64-windows.2\bin\java.exe'
    if (-not (Test-Path $java)) {
        Write-Host "LintBudget skipped=missing-java issues=0"
        $script:skippedChecks++
        return 0
    }
    $harness = Join-Path $env:TEMP 'RmuixStaticLint.java'
    $editorconfig = Join-Path $env:TEMP 'rmuix-review.editorconfig'
    if (-not (Test-Path $harness) -or -not (Test-Path $editorconfig)) {
        Write-Host "LintBudget skipped=missing-harness issues=0"
        $script:skippedChecks++
        Write-Host "  The harness lives in %TEMP% (RmuixStaticLint.java + rmuix-review.editorconfig) and is not"
        Write-Host "  checked in; see tools/README.md for how to recreate it."
        return 0
    }

    $files = Get-ChildItem $SrcDirs -Recurse -Include '*.kt' -File | Select-Object -ExpandProperty FullName
    $cp = ($wanted | ForEach-Object {
        Get-ChildItem (Join-Path $cache $_) -Recurse -Filter *.jar -File |
            Where-Object { $_.Name -notmatch 'sources|javadoc' } |
            Select-Object -ExpandProperty FullName
    }) -join ';'
    $classOut = Join-Path $env:TEMP 'rmuix_lint_classes'
    if (-not (Test-Path $classOut)) { New-Item -ItemType Directory -Path $classOut -Force | Out-Null }
    $javac = Join-Path (Split-Path $java) 'javac.exe'
    # javac writes a notes/warnings block to stderr; keep it out of the transcript and out of $output.
    $previous = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    & $javac -cp $cp -d $classOut $harness 2>&1 | Out-Null
    $ErrorActionPreference = $previous
    $arguments = @('-cp', ($cp + ';' + $classOut), 'RmuixStaticLint', $editorconfig, 'lint') + $files
    $previous = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    # java prints an SLF4J banner and ktlint's own lint reports to stderr; drop the streams we do not parse.
    # A syntax error makes ktlint throw `KtLintParseException` and stop, which used to look exactly like a
    # clean run with fewer violations. Capture stderr so the check can say *which file* failed to parse.
    $parseErrors = Join-Path $env:TEMP 'rmuix_lint_stderr.txt'
    $output = & $java @arguments 2> $parseErrors
    $ErrorActionPreference = $previous
    $parseLog = if (Test-Path $parseErrors) { Get-Content -LiteralPath $parseErrors -Encoding UTF8 -ErrorAction SilentlyContinue } else { @() }
    $parseFailures = @($parseLog | Where-Object { $_ -match 'KtLintParseException|Expecting|Unexpected tokens' })
    $rules = $output | Where-Object { $_ -match 'RuleId' }
    $counts = @{}
    foreach ($line in $rules) {
        $rule = ([regex]::Match($line, 'RuleId\(value=([\w:.-]+)')).Groups[1].Value
        if (-not $rule) { continue }
        if (-not $counts.ContainsKey($rule)) { $counts[$rule] = 0 }
        $counts[$rule]++
    }
    $total = $rules.Count
    $issues = New-Object System.Collections.ArrayList
    # Both directions matter. Fewer violations than the baseline is usually good news, but it is also what
    # a *partial* run looks like (a parse error makes the linter stop early and report fewer lines), so the
    # check insists on an exact match: update the numbers deliberately whenever the count moves.
    foreach ($rule in $baseline.Keys) {
        $now = if ($counts.ContainsKey($rule)) { $counts[$rule] } else { 0 }
        if ($now -ne $baseline[$rule]) {
            [void]$issues.Add("$rule is $now, baseline records $($baseline[$rule]) - update the baseline after a deliberate change")
        }
    }
    foreach ($rule in $counts.Keys | Sort-Object) {
        if (-not $baseline.Contains($rule) -and $counts[$rule] -gt 0) {
            [void]$issues.Add("$rule is new ($($counts[$rule]) occurrences) and not in the baseline")
        }
    }
    if ($total -ne $baselineTotal) {
        [void]$issues.Add("total violations is $total, baseline records $baselineTotal - a large drop usually means the linter stopped early (parse error)")
    }
    # A parse failure is a hard error, not a heuristic: the file does not compile, and the violation count
    # that follows it is meaningless. Report the first failing message so the fix starts from the right file.
    if ($parseFailures.Count -gt 0) {
        [void]$issues.Add("ktlint could not parse a file ($($parseFailures.Count) stderr lines); the violation count above is not a census")
        foreach ($line in ($parseFailures | Select-Object -First 3)) { [void]$issues.Add("  $($line.Trim())") }
    }
    Write-Host ("LintBudget files={0} violations={1} baseline={2} rules={3}" -f $files.Count, $total, $baselineTotal, $counts.Count)
    foreach ($issue in $issues) { Write-Host "  $issue" }
    return $issues.Count
}

function Invoke-PlanSyncCheck {
    # The review index carries a per-batch file list for the pending commit ("各批次文件清单"). This check
    # keeps that list honest: every path it mentions must still be dirty, and every dirty path must appear
    # in exactly one batch. Without it the plan silently rots as the work continues.
    $index = Join-Path $Root 'docs\dev\status-snapshot.md'
    if (-not (Test-Path $index)) {
        Write-Host "PlanSync skipped=missing-index issues=0"
        $script:skippedChecks++
        return 0
    }
    $listed = New-Object System.Collections.ArrayList
    $inBlock = $false
    $seenSection = $false
    foreach ($line in Get-Content -LiteralPath $index -Encoding UTF8) {
        # Only a ```text block that sits under the "## 九、提交批次" heading is the commit plan. Without
        # this guard a rewrite that drops the heading still passes: the check used to take the last
        # ```text block anywhere in the file, which is how the heading was lost on 2026-11-14 without
        # any check complaining.
        if ($line -match '^##\s*九') { $seenSection = $true; continue }
        if (-not $seenSection) { continue }
        if ($line -match '^```text') { $inBlock = $true; continue }
        if ($inBlock -and $line -match '^```') { $inBlock = $false; continue }
        if ($inBlock -and $line.Trim()) {
            [void]$listed.Add(($line.Trim() -replace '^(?:M|A|\?\?)\s+', ''))
        }
    }
    if ($listed.Count -eq 0) {
        # A missing plan is a defect, not a skip: the snapshot is the single status entry and the commit
        # plan section is part of its contract. This used to `return 0`, which is how a rewrite that
        # dropped the `## \u4e5d` heading passed silently on 2026-11-14.
        Write-Host "PlanSync listed=0 issues=1"
        Write-Host '  status-snapshot.md has no file list under the section-9 heading'
        return 1
    }
    $dirty = New-Object System.Collections.ArrayList
    $renames = 0
    foreach ($line in (& git -C $Root status --porcelain --untracked-files=all)) {
        if (-not $line.Trim()) { continue }
        # Pure path moves (`git mv`) carry no content change, so the plan does not enumerate them;
        # they are reported as a count instead. Anything with edits still shows up as M/A.
        # Porcelain v1 is `XY PATH`: a deletion is either staged (`D `) or still only in the worktree
        # (` D`). Matching `^D` only caught the staged form, so a plain `git reset` - which turns every
        # staged deletion into ` D` - flooded the check with 1478 phantom "dirty but missing from the
        # plan" entries on 2026-11-16. Compare the two status columns instead of the raw line start.
        $code = $line.Substring(0, 2)
        if ($code -match 'R') { $renames++; continue }
        if ($code -match 'D') { continue }
        [void]$dirty.Add((($line.Substring(3)).Trim('"').Replace('\', '/')))
    }
    $listedNorm = $listed | ForEach-Object { $_.Replace('\', '/') }
    $issues = New-Object System.Collections.ArrayList
    foreach ($path in $listedNorm) {
        if ($dirty -notcontains $path) { [void]$issues.Add("$path is listed in the commit plan but is no longer dirty") }
    }
    foreach ($path in $dirty) {
        $hits = @($listedNorm | Where-Object { $_ -eq $path })
        if ($hits.Count -eq 0) { [void]$issues.Add("$path is dirty but missing from the commit plan") }
        elseif ($hits.Count -gt 1) { [void]$issues.Add("$path appears $($hits.Count) times in the commit plan") }
    }
    Write-Host ("PlanSync listed={0} dirty={1} renames={2} issues={3}" -f $listed.Count, $dirty.Count, $renames, $issues.Count)
    # Capped: a tree that is mid mass-rename turns this into >1000 lines, which buries the other checks.
    $issues | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    if ($issues.Count -gt 20) { Write-Host ("  ... {0} more" -f ($issues.Count - 20)) }
    return $issues.Count
}


function Invoke-ShadowShapeCheck {
    # A `dropShadow` whose shape is a `RoundedCornerShape` while the same modifier chain paints a
    # `squircleBackground` / `squircleSurface` cast a shadow with a different silhouette than the fill.
    # Both sides of the pair are text-detected inside a 25-line window, which is enough for every
    # occurrence in this repo and keeps the check conservative (it cannot prove a chain boundary).
    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($dir in @('rmuix-ui\src', 'rmuix-preference\src')) {
        $root = Join-Path $Root $dir
        if (-not (Test-Path $root)) { continue }
        foreach ($file in Get-ChildItem $root -Recurse -Include '*.kt' -File) {
            $lines = (Read-Text $file.FullName) -split "`n"
            $roundedShapes = New-Object 'System.Collections.Generic.HashSet[string]'
            foreach ($m in [regex]::Matches(($lines -join "`n"), 'val\s+(shape\w*)\s*=\s*RoundedCornerShape\(')) {
                [void]$roundedShapes.Add($m.Groups[1].Value)
            }
            for ($i = 0; $i -lt $lines.Count; $i++) {
                if ($lines[$i] -notmatch '\.dropShadow\(') { continue }
                $window = $lines[$i..([Math]::Min($lines.Count - 1, $i + 25))] -join "`n"
                if ($window -notmatch 'squircleBackground\(|squircleSurface\(') { continue }
                $checked++
                $isRounded = $window -match 'shape\s*=\s*RoundedCornerShape\('
                if (-not $isRounded) {
                    foreach ($name in $roundedShapes) {
                        if ($window -match ('shape\s*=\s*' + [regex]::Escape($name) + '\b')) { $isRounded = $true; break }
                    }
                }
                if ($isRounded) {
                    [void]$issues.Add(("{0}:{1} dropShadow uses a RoundedCornerShape in a squircle chain" -f $file.Name, ($i + 1)))
                }
            }
            # `.clip(RoundedCornerShape(...))` followed by `.squircleBackground(...)` clips the squircle
            # back to a circular arc, defeating the continuous silhouette.
            for ($i = 0; $i -lt $lines.Count; $i++) {
                if ($lines[$i] -notmatch '\.clip\(') { continue }
                $window = $lines[$i..([Math]::Min($lines.Count - 1, $i + 15))] -join "`n"
                if ($window -notmatch 'squircleBackground\(|squircleSurface\(') { continue }
                $shapeText = ($lines[$i..([Math]::Min($lines.Count - 1, $i + 2))] -join "`n")
                $isRoundedClip = $shapeText -match 'clip\(\s*RoundedCornerShape\('
                if (-not $isRoundedClip) {
                    foreach ($name in $roundedShapes) {
                        if ($shapeText -match ('clip\(\s*' + [regex]::Escape($name) + '\b')) { $isRoundedClip = $true; break }
                    }
                }
                if ($isRoundedClip) {
                    [void]$issues.Add(("{0}:{1} clip uses a RoundedCornerShape in a squircle chain" -f $file.Name, ($i + 1)))
                }
            }
            # A `SquircleShape(...)` in a file that draws squircle surfaces must carry `squircleEnabled`, or
            # it keeps the continuous silhouette while the fill degrades to a rounded rect.
            if (($lines -join "`n") -match 'squircleBackground\(|squircleSurface\(') {
                for ($i = 0; $i -lt $lines.Count; $i++) {
                    if ($lines[$i] -notmatch 'SquircleShape\(') { continue }
                    $call = $lines[$i..([Math]::Min($lines.Count - 1, $i + 4))] -join "`n"
                    if ($call -match 'SquircleShape\([^)]*squircleEnabled') { continue }
                    [void]$issues.Add(("{0}:{1} SquircleShape without squircleEnabled in a squircle file" -f $file.Name, ($i + 1)))
                }
            }
        }
    }

    Write-Host ("ShadowShape checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

function Invoke-ResearchHeadingsCheck {
    # Inserting a section into a numbered document is how the roadmap ended up with two `## \u4e94` headings
    # (2026-11-14) and how status-snapshot lost its `## \u4e5d` heading the day before. Both are invisible to
    # every other check, so assert the invariants directly: numbered `##` headings must be unique, and so
    # must any repeated `##` title.
    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    $researchRoot = Join-Path $Root 'docs\dev'
    if (Test-Path $researchRoot) {
        foreach ($file in Get-ChildItem $researchRoot -Filter '*.md' -File) {
            $checked++
            $numbers = New-Object System.Collections.ArrayList
            $titles = New-Object System.Collections.ArrayList
            foreach ($line in ((Read-Text $file.FullName) -split "`n")) {
                if ($line -notmatch '^##\s+(.+)$') { continue }
                $heading = $matches[1].Trim()
                $num = [regex]::Match($heading, '^([\u4e00-\u9fa5]{1,3})\u3001')
                if ($num.Success) { [void]$numbers.Add($num.Groups[1].Value) }
                [void]$titles.Add($heading)
            }
            # Order matters too: appending a section while rebuilding a document is how `status-snapshot`
            # ended up with \u5341 before \u4e5d (2026-11-14) - unique numbers, wrong sequence.
            $rank = @{ '一' = 1; '二' = 2; '三' = 3; '四' = 4; '五' = 5; '六' = 6; '七' = 7; '八' = 8; '九' = 9; '十' = 10 }
            $lastRank = 0
            foreach ($n in $numbers) {
                if (-not $rank.ContainsKey($n)) { continue }
                $r = $rank[$n]
                if ($r -lt $lastRank) {
                    [void]$issues.Add(("{0}: chapter {1} appears after chapter {2}" -f $file.Name, $n, $lastRank))
                }
                $lastRank = [Math]::Max($lastRank, $r)
            }
            foreach ($group in ($numbers | Group-Object | Where-Object { $_.Count -gt 1 })) {
                [void]$issues.Add(("{0}: chapter heading '{1}' appears {2} times" -f $file.Name, $group.Name, $group.Count))
            }
            foreach ($group in ($titles | Group-Object | Where-Object { $_.Count -gt 1 })) {
                [void]$issues.Add(("{0}: heading '{1}' appears {2} times" -f $file.Name, $group.Name, $group.Count))
            }
        }
    }
    Write-Host ("ResearchHeadings checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

function Invoke-TestWritePathsCheck {
    # A test that opens a file with a *relative* path writes into the Gradle module directory, not the
    # repo root: `File("docs/dev/shots")` in `rmuix-ui/src/desktopTest` created a stray
    # `rmuix-ui/docs/...` tree on every run, and that tree is not gitignored. Two screenshot tests did
    # exactly this. The convention is to walk up until `settings.gradle.kts` is found.
    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($module in @('rmuix-ui', 'rmuix-preference', 'rmuix-extend', 'rmuix-nav', 'rmuix-window', 'rmuix-shader', 'rmuix-blur', 'rmuix-squircle')) {
        $src = Join-Path $Root (Join-Path $module 'src')
        if (-not (Test-Path $src)) { continue }
        foreach ($file in Get-ChildItem $src -Recurse -Include '*Test*.kt' -File) {
            $checked++
            $lines = (Read-Text $file.FullName) -split "`n"
            for ($i = 0; $i -lt $lines.Count; $i++) {
                # `File("...")` / `File(".../x.png")` with a non-absolute literal is the defect; calls
                # that pass a resolved variable/root (`File(root, "...")`) are fine.
                # Case-sensitive and word-bounded: `-match` is case-insensitive, so a bare `File(` also
                # matched `RestoreProfile("yu")`. Paths under the module's own `build/` output are fine.
                if ($lines[$i] -cmatch '\bFile\(\s*"[^"\\/][^"]*"' -and
                    $lines[$i] -notmatch 'System\.getProperty' -and
                    $lines[$i] -notmatch 'File\(\s*"build/') {
                    [void]$issues.Add(("{0}/{1}:{2} relative file path - resolve the repo root first" -f $module, $file.Name, ($i + 1)))
                }
            }
        }
    }
    Write-Host ("TestWritePaths checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

function Invoke-SerializableScopeCheck {
    # `@Serializable sealed interface Route` produces a *closed* polymorphic serializer whose scope is
    # exactly the members that are themselves `@Serializable`. Dropping one of those annotations is
    # not a compile error - the member silently leaves the scope, and the first `rememberSaveable` of
    # a savable state that encodes it throws at runtime:
    #   JsonEncodingException: Serializer for subclass 'X' is not found in the polymorphic scope of 'Y'
    # That is what killed the example app on 2026-11-16: a text edit stripped six subclass
    # annotations from `example/shared/.../navigation/Route.kt`, every build and all 579 unit tests
    # stayed green, and the app died on first composition.
    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($file in Get-ChildItem $Root -Recurse -Include '*.kt' -File) {
        if ($file.FullName -match '\\build\\|\\\.git\\|\\\.gradle\\') { continue }
        $text = Read-Text $file.FullName
        if ($text -notmatch '@Serializable') { continue }
        if ($text -notmatch 'sealed\s+(interface|class)') { continue }

        $lines = $text -split "`n"
        # Brace depth per line, computed on a copy with comments and string literals removed, so a
        # `{` inside a comment or a default value cannot shift the nesting.
        $depth = New-Object 'int[]' $lines.Count
        $running = 0
        for ($i = 0; $i -lt $lines.Count; $i++) {
            $depth[$i] = $running
            $code = $lines[$i] -replace '//.*$', ''
            $code = $code -replace '"(?:\\.|[^"\\])*"', '""'
            $running += ([regex]::Matches($code, '\{')).Count - ([regex]::Matches($code, '\}')).Count
        }

        for ($i = 0; $i -lt $lines.Count; $i++) {
            if ($lines[$i] -notmatch 'sealed\s+(interface|class)\s+\w+') { continue }
            # The sealed declaration itself must be annotated for the scope to exist at all.
            $annotated = $lines[$i] -match '@Serializable'
            if (-not $annotated) {
                for ($j = $i - 1; $j -ge 0 -and $j -ge $i - 3; $j--) {
                    if ($lines[$j].Trim() -eq '') { continue }
                    $annotated = $lines[$j].Trim() -match '^@Serializable'
                    break
                }
            }
            if (-not $annotated) { continue }

            $base = $depth[$i]
            for ($k = $i + 1; $k -lt $lines.Count; $k++) {
                if ($depth[$k] -le $base) { break }
                if ($depth[$k] -ne $base + 1) { continue }
                $line = $lines[$k]
                # Direct members: `data object X`, `data class X`, `object X`, `class X`.
                if ($line -notmatch '^\s+(?:@\w+\s+)*(?:data\s+)?(?:object|class)\s+\w+') { continue }
                $checked++
                if ($line -match '@Serializable') { continue }
                $ok = $false
                for ($m = $k - 1; $m -ge 0; $m--) {
                    $prev = $lines[$m].Trim()
                    if ($prev -eq '') { continue }
                    $ok = $prev -match '^@Serializable'
                    break
                }
                if (-not $ok) {
                    $member = ($line.Trim() -replace '\s*[:(].*$', '')
                    [void]$issues.Add(("{0}:{1} {2} is not @Serializable - it is invisible to the sealed serializer" -f $file.Name, ($k + 1), $member))
                }
            }
        }
    }
    Write-Host ("SerializableScope checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

function Invoke-BreakingReversalCheck {
    # A `**BREAKING:**` note records the migration direction ("collapsed width 80dp -> 72dp"). If the
    # same page later documents the reverse move ("changed back from 72dp to 80dp"), the two statements
    # contradict each other - which is exactly what happened to NavigationRail: its BREAKING note still
    # claimed 80dp -> 72dp after the default had been reverted to 80dp.
    #
    # Rule: collect `A -> B` value pairs. A pair that appears inside a BREAKING line and whose reverse
    # (`B -> A`) is documented anywhere else on the same page is a contradiction.
    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    $pair = '([0-9]+(?:\.[0-9]+)?(?:dp|sp|%))'
    foreach ($dirName in @('docs\components', 'docs\zh_CN')) {
        $dirPath = Join-Path $Root $dirName
        if (-not (Test-Path $dirPath)) { continue }
        foreach ($file in Get-ChildItem $dirPath -Recurse -Include '*.md' -File) {
            $checked++
            $text = Read-Text $file.FullName
            $breaking = @{}
            $elsewhere = @{}
            foreach ($line in ($text -split "`n")) {
                $isBreaking = $line -match '\*\*BREAKING'
                foreach ($m in [regex]::Matches($line, ($pair + '\s*(?:→|->)\s*' + $pair))) {
                    $key = "$($m.Groups[1].Value)->$($m.Groups[2].Value)"
                    if ($isBreaking) { $breaking[$key] = $true } else { $elsewhere[$key] = $true }
                }
            }
            foreach ($key in $breaking.Keys) {
                $parts = $key -split '->'
                $reverse = "$($parts[1])->$($parts[0])"
                if ($elsewhere.ContainsKey($reverse)) {
                    [void]$issues.Add(("{0}: BREAKING says {1} but the page also documents {2}" -f $file.Name, $key, $reverse))
                }
            }
        }
    }
    Write-Host ("BreakingReversal checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

function Invoke-ViewportUnitsCheck {
    # A drawing block that already scaled the canvas into the source vector's unit space (e.g.
    # `scale(size.width / 24f, ...)`) works in *viewport units*, not dp. Calling `.toPx()` inside such a
    # block multiplies by the density a second time: 16.4dp became 51dp and the unchecked Checkbox ring
    # wrapped far outside its 24dp box. ktlint, the other checks and the unit tests are all blind to it
    # because the token value is right and only its unit is wrong.
    #
    # Two rules, because the defect shows up in both shapes:
    #   1. `.toPx()` written directly in the 120 lines after the `scale(...)` call;
    #   2. `.toPx()` inside a helper `fun` that is *called* from those 120 lines - `drawCheckboxResting`
    #      lives 270 lines below its call site, so a window-only rule misses half the defects.
    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($dir in @('rmuix-ui\src\commonMain', 'rmuix-preference\src\commonMain')) {
        $root = Join-Path $Root $dir
        if (-not (Test-Path $root)) { continue }
        foreach ($file in Get-ChildItem $root -Recurse -Include '*.kt' -File) {
            $lines = (Read-Text $file.FullName) -split "`n"
            if (($lines -join "`n") -notmatch 'scale\(\s*size\.width\s*/\s*\d') { continue }
            # index every function body once: name -> @(startLine, endLine) (1-based)
            $bodies = @{}
            for ($i = 0; $i -lt $lines.Count; $i++) {
                $m = [regex]::Match($lines[$i], '^\s*(?:private |internal |public |override )*fun\s+(?:DrawScope\.)?(\w+)\s*\(')
                if (-not $m.Success) { continue }
                $depth = 0
                $opened = $false
                $end = $i
                for ($k = $i; $k -lt $lines.Count; $k++) {
                    $depth += ([regex]::Matches($lines[$k], '\{')).Count
                    $depth -= ([regex]::Matches($lines[$k], '\}')).Count
                    if ($depth -gt 0) { $opened = $true }
                    if ($opened -and $depth -le 0) { $end = $k; break }
                }
                if (-not $bodies.ContainsKey($m.Groups[1].Value)) { $bodies[$m.Groups[1].Value] = @(($i + 1), ($end + 1)) }
            }
            for ($i = 0; $i -lt $lines.Count; $i++) {
                if ($lines[$i] -notmatch 'scale\(\s*size\.width\s*/\s*(\d+(?:\.\d+)?)f') { continue }
                $viewport = [double]$Matches[1]
                $checked++
                $windowEnd = [Math]::Min($lines.Count - 1, $i + 120)
                $window = $lines[$i..$windowEnd]
                $lineNo = $i + 2
                foreach ($line in $window) {
                    if ($line -match '\.toPx\(\)') {
                        [void]$issues.Add(("{0}:{1} .toPx() inside a {2}-unit viewport scale (use .value)" -f $file.Name, $lineNo, $viewport))
                    }
                    $lineNo++
                }
                # rule 2: helpers called from the window
                $called = @()
                foreach ($line in $window) {
                    foreach ($cm in [regex]::Matches($line, '(?<![\w.])(\w+)\s*\(')) {
                        $name = $cm.Groups[1].Value
                        if ($bodies.ContainsKey($name) -and $called -notcontains $name) { $called += $name }
                    }
                }
                foreach ($name in $called) {
                    $span = $bodies[$name]
                    for ($k = $span[0] - 1; $k -lt [Math]::Min($lines.Count, $span[1]); $k++) {
                        if ($lines[$k] -match '\.toPx\(\)') {
                            [void]$issues.Add(("{0}:{1} .toPx() inside `{2}`, a helper of the {3}-unit viewport block (use .value)" -f $file.Name, ($k + 1), $name, $viewport))
                        }
                    }
                }
            }
        }
    }

    Write-Host ("ViewportUnits checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}

function Invoke-UnusedParamsCheck {
    # A public composable parameter that appears exactly once in its file - in the declaration - is a
    # silent no-op: callers can pass it and nothing happens. Only `@Composable fun` declarations are
    # inspected, so interface methods and plain helpers do not produce noise.
    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($dir in @('rmuix-ui\src\commonMain', 'rmuix-preference\src\commonMain')) {
        $root = Join-Path $Root $dir
        if (-not (Test-Path $root)) { continue }
        foreach ($file in Get-ChildItem $root -Recurse -Include '*.kt' -File) {
            $text = Read-Text $file.FullName
            $lines = $text -split "`n"
            for ($i = 1; $i -lt $lines.Count; $i++) {
                if ($lines[$i] -notmatch '^\s*(private |internal |public )?fun \w+\(') { continue }
                if ($lines[$i - 1] -notmatch '@Composable') { continue }
                $depth = 0
                $j = $i
                $buffer = ''
                do {
                    $buffer += $lines[$j] + "`n"
                    $depth += ([regex]::Matches($lines[$j], '\(')).Count - ([regex]::Matches($lines[$j], '\)')).Count
                    $j++
                } while ($depth -gt 0 -and $j -lt [Math]::Min($lines.Count, $i + 40))
                $params = New-Object 'System.Collections.Generic.HashSet[string]'
                foreach ($m in [regex]::Matches($buffer, '(?m)^\s{4,}(\w+)\s*:\s*[^,=]+(?:=[^,\n]+)?,')) {
                    [void]$params.Add($m.Groups[1].Value)
                }
                foreach ($name in $params) {
                    $checked++
                    $uses = ([regex]::Matches($text, ('\b' + [regex]::Escape($name) + '\b'))).Count
                    if ($uses -le 1) {
                        [void]$issues.Add(("{0}:{1} parameter '{2}' is declared but never used" -f $file.Name, $i, $name))
                    }
                }
            }
        }
    }

    Write-Host ("UnusedParams checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}
function Invoke-DefaultsResolveCheck {
    # `XxxDefaults.Member = SOME_CONSTANT` where `SOME_CONSTANT` is declared nowhere in the module is a
    # compile error that neither ktlint nor the other text checks can see (they never resolve names).
    $declared = New-Object 'System.Collections.Generic.HashSet[string]'
    $files = @()
    foreach ($dir in @('rmuix-ui\src\commonMain', 'rmuix-preference\src\commonMain', 'rmuix-squircle\src\commonMain')) {
        $root = Join-Path $Root $dir
        if (-not (Test-Path $root)) { continue }
        $files += Get-ChildItem $root -Recurse -Include '*.kt' -File
    }
    $allText = ($files | ForEach-Object { Read-Text $_.FullName }) -join "`n"
    foreach ($m in [regex]::Matches($allText, '(?m)^\s*(?:internal |public |private )?(?:const )?(?:val|fun|var) ([A-Za-z_][A-Za-z0-9_]*)')) {
        [void]$declared.Add($m.Groups[1].Value)
    }

    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($file in $files) {
        $lines = (Read-Text $file.FullName) -split "`n"
        for ($i = 0; $i -lt $lines.Count; $i++) {
            foreach ($m in [regex]::Matches($lines[$i], '^\s*(?:const )?val \w+\s*=\s*([A-Z][A-Z0-9_]{3,})\s*$')) {
                $checked++
                $id = $m.Groups[1].Value
                if (-not $declared.Contains($id)) {
                    [void]$issues.Add(("{0}:{1} '{2}' is referenced but declared nowhere" -f $file.Name, ($i + 1), $id))
                }
            }
        }
    }

    Write-Host ("DefaultsResolve checked={0} issues={1}" -f $checked, $issues.Count)
    $issues | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}
function Invoke-ThemeMembersCheck {
    # `RmuixTheme.colorScheme.<member>` / `.textStyles.<member>` typos are compile errors no other check
    # sees. The declared member sets are read straight from `Colors.kt` and `TextStyles.kt`.
    $themeDir = Join-Path $Root 'rmuix-ui\src\commonMain\kotlin\io\github\ra1nify\rmuix\kmp\theme'
    $colorFile = Join-Path $themeDir 'Colors.kt'
    $textFile = Join-Path $themeDir 'TextStyles.kt'
    if (-not (Test-Path $colorFile) -or -not (Test-Path $textFile)) {
        Write-Host "ThemeMembers skipped (theme sources not found)"
        $script:skippedChecks++
        return 0
    }

    $colors = New-Object 'System.Collections.Generic.HashSet[string]'
    foreach ($m in [regex]::Matches((Read-Text $colorFile), '(?m)^\s*var (\w+) by mutableStateOf\(')) { [void]$colors.Add($m.Groups[1].Value) }
    $textStyles = New-Object 'System.Collections.Generic.HashSet[string]'
    foreach ($m in [regex]::Matches((Read-Text $textFile), '(?m)^\s*var (\w+) by mutableStateOf\(')) { [void]$textStyles.Add($m.Groups[1].Value) }
    foreach ($m in [regex]::Matches((Read-Text $textFile), '(?m)^\s*val (\w+)\s*:')) { [void]$textStyles.Add($m.Groups[1].Value) }

    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($dir in @('rmuix-ui\src', 'rmuix-preference\src')) {
        $root = Join-Path $Root $dir
        if (-not (Test-Path $root)) { continue }
        foreach ($file in Get-ChildItem $root -Recurse -Include '*.kt' -File) {
            $lines = (Read-Text $file.FullName) -split "`n"
            for ($i = 0; $i -lt $lines.Count; $i++) {
                foreach ($m in [regex]::Matches($lines[$i], 'colorScheme\.(\w+)')) {
                    $checked++
                    if (-not $colors.Contains($m.Groups[1].Value)) {
                        [void]$issues.Add(("{0}:{1} colorScheme.{2} is not a Colors member" -f $file.Name, ($i + 1), $m.Groups[1].Value))
                    }
                }
                foreach ($m in [regex]::Matches($lines[$i], 'textStyles\.(\w+)')) {
                    $member = $m.Groups[1].Value
                    if ($member -in @('copy', 'equals', 'hashCode', 'toString')) { continue }
                    $checked++
                    if (-not $textStyles.Contains($member)) {
                        [void]$issues.Add(("{0}:{1} textStyles.{2} is not a TextStyles member" -f $file.Name, ($i + 1), $member))
                    }
                }
            }
        }
    }

    Write-Host ("ThemeMembers checked={0} colors={1} textStyles={2} issues={3}" -f $checked, $colors.Count, $textStyles.Count, $issues.Count)
    $issues | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}
function Invoke-ObjectMembersCheck {
    # `SomeObject.member` where `SomeObject` is one of this repo's top-level objects but `member` is not
    # declared inside it is a compile error. Unlike the theme check this is a *general* roster: every
    # `^object X {` at top level. Objects with supertypes are skipped (their members may be inherited).
    $files = @()
    foreach ($dir in @('rmuix-ui\src\commonMain', 'rmuix-preference\src\commonMain', 'rmuix-squircle\src\commonMain')) {
        $root = Join-Path $Root $dir
        if (Test-Path $root) { $files += Get-ChildItem $root -Recurse -Include '*.kt' -File }
    }

    $memberPattern = '(?m)^\s{4,}(?:@\w+(?:\([^)]*\))?\s*)*(?:internal |public |private |override )?(?:const )?(?:val|var|fun|class|enum class|data class|sealed interface|interface|object)\s+(?:<[^>\r\n]+>\s*)?(\w+)'
    $objects = @{}
    foreach ($file in $files) {
        $text = Read-Text $file.FullName
        foreach ($m in [regex]::Matches($text, '(?m)^(?:(?:internal|public|private)\s+)?object\s+(\w+)\s*\{')) {
            $name = $m.Groups[1].Value
            $start = $m.Index + $m.Length - 1
            $depth = 0
            $end = $start
            for ($i = $start; $i -lt $text.Length; $i++) {
                if ($text[$i] -eq '{') { $depth++ } elseif ($text[$i] -eq '}') { $depth--; if ($depth -eq 0) { $end = $i; break } }
            }
            $body = $text.Substring($start, $end - $start)
            $members = New-Object 'System.Collections.Generic.HashSet[string]'
            foreach ($mm in [regex]::Matches($body, $memberPattern)) { [void]$members.Add($mm.Groups[1].Value) }
            if (-not $objects.ContainsKey($name)) { $objects[$name] = $members }
        }
    }

    $checked = 0
    $issues = New-Object System.Collections.ArrayList
    foreach ($file in $files) {
        $text = Read-Text $file.FullName
        foreach ($name in $objects.Keys) {
            if ($objects[$name].Count -eq 0) { continue }
            foreach ($m in [regex]::Matches($text, ('\b' + [regex]::Escape($name) + '\.(\w+)'))) {
                $checked++
                if (-not $objects[$name].Contains($m.Groups[1].Value)) {
                    [void]$issues.Add(("{0} {1}.{2} is not a member of {1}" -f $file.Name, $name, $m.Groups[1].Value))
                }
            }
        }
    }

    Write-Host ("ObjectMembers checked={0} objects={1} issues={2}" -f $checked, $objects.Count, $issues.Count)
    $issues | Sort-Object -Unique | Select-Object -First 20 | ForEach-Object { Write-Host "  $_" }
    return $issues.Count
}
if ($Check -in @('Tokens', 'All')) { $total += Invoke-TokenCheck }
if ($Check -in @('DocsTokens', 'All')) { $total += Invoke-DocsTokensCheck }
if ($Check -in @('DocVsApk', 'All')) { $total += Invoke-DocVsApkCheck }
if ($Check -in @('DocVsCode', 'All')) { $total += Invoke-DocVsCodeCheck }
if ($Check -in @('DocsValues', 'All')) { $total += Invoke-DocsValuesCheck }
if ($Check -in @('DocsPair', 'All')) { $total += Invoke-DocsPairCheck }
if ($Check -in @('DocsDefaults', 'All')) { $total += Invoke-DocsDefaultsCheck }
if ($Check -in @('TestsVsCode', 'All')) { $total += Invoke-TestsVsCodeCheck }
if ($Check -in @('DocsRefs', 'All')) { $total += Invoke-DocsRefsCheck }
if ($Check -in @('StockVsApk', 'All')) { $total += Invoke-StockVsApkCheck }
if ($Check -in @('TokenLiveness', 'All')) { $total += Invoke-TokenLivenessCheck }
if ($Check -in @('ResearchValues', 'All')) { $total += Invoke-ResearchValuesCheck }
if ($Check -in @('ColorVsApk', 'All')) { $total += Invoke-ColorVsApkCheck }
if ($Check -in @('DefaultsRefs', 'All')) { $total += Invoke-DefaultsRefsCheck }
if ($Check -in @('LintBudget', 'All')) { $total += Invoke-LintBudgetCheck }
if ($Check -in @('PlanSync', 'All')) { $total += Invoke-PlanSyncCheck }
if ($Check -in @('ShadowShape', 'All')) { $total += Invoke-ShadowShapeCheck }
if ($Check -in @('UnusedParams', 'All')) { $total += Invoke-UnusedParamsCheck }
if ($Check -in @('DefaultsResolve', 'All')) { $total += Invoke-DefaultsResolveCheck }
if ($Check -in @('ThemeMembers', 'All')) { $total += Invoke-ThemeMembersCheck }
if ($Check -in @('ObjectMembers', 'All')) { $total += Invoke-ObjectMembersCheck }
if ($Check -in @('ViewportUnits', 'All')) { $total += Invoke-ViewportUnitsCheck }
if ($Check -in @('BreakingReversal', 'All')) { $total += Invoke-BreakingReversalCheck }
if ($Check -in @('TestWritePaths', 'All')) { $total += Invoke-TestWritePathsCheck }
if ($Check -in @('SerializableScope', 'All')) { $total += Invoke-SerializableScopeCheck }
if ($Check -in @('ResearchHeadings', 'All')) { $total += Invoke-ResearchHeadingsCheck }

Write-Host ""
if ($script:skippedChecks -gt 0) {
    Write-Host ("{0} check(s) were SKIPPED (missing environment prerequisite); the run is not a full verification." -f $script:skippedChecks)
    Write-Host ""
}
if ($total -eq 0) {
    Write-Host "No issues found. Reminder: these checks are text heuristics; confirm anything surprising by hand."
    exit 0
}
Write-Host "$total issue(s) need a human decision. See the heuristic limits in tools/README.md."
exit 1
