# Auto-generated by EclipseNSIS Script Wizard
# 01.09.2010 18:10:16

Name ColibriTS

# General Symbol Definitions
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 3.7.2_2.0.58.201701201600
!define COMPANY "Christian Eugster"
!define URL http://eugster-informatik.ch/
!define PRODUCT_PATH C:\Projekte\Colibrits2\Product\release-${VERSION}
!define COMMON_PATH C:\Projekte\Colibrits2\Install\common

# MUI Symbol Definitions
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install-blue-full.ico"
!define MUI_FINISHPAGE_AUTOCLOSE
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${REGKEY}
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULTFOLDER ColibriTS
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall-colorful.ico"
#!define MUI_UNFINISHPAGE_NOAUTOCLOSE



# Included files
!include MultiUser.nsh
!include Sections.nsh
!include MUI2.nsh

# Variables
Var StartMenuGroup

# Installer pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup
!insertmacro MUI_PAGE_INSTFILES
#!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Installer languages
!insertmacro MUI_LANGUAGE German
!insertmacro MUI_LANGUAGE English

# Installer attributes
OutFile "C:\Projekte\Colibrits2\Setup\colibri_setup_win32.x86_${VERSION}.exe"
installDir C:\ColibriTSII
CRCCheck on
XPStyle on
ShowInstDetails show
VIProductVersion 1.0.0.0
VIAddVersionKey /LANG=${LANG_GERMAN} ProductName ColibriTS
VIAddVersionKey /LANG=${LANG_GERMAN} ProductVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_GERMAN} CompanyName "${COMPANY}"
VIAddVersionKey /LANG=${LANG_GERMAN} CompanyWebsite "${URL}"
VIAddVersionKey /LANG=${LANG_GERMAN} FileVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_GERMAN} FileDescription ""
VIAddVersionKey /LANG=${LANG_GERMAN} LegalCopyright ""
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails show

# Installer sections
Section -Main SEC0000
    SetOverwrite on
    SetOutPath $INSTDIR\configuration
    File /r ${PRODUCT_PATH}\configuration\*
;    SetOutPath $INSTDIR\dll
;    File /r ${COMMON_PATH}\dll\*
    SetOutPath $INSTDIR\features
    File /r ${PRODUCT_PATH}\features\*
    SetOutPath $INSTDIR\p2
    File /r ${PRODUCT_PATH}\p2\*
    SetOutPath $INSTDIR\jre
    File /r ${COMMON_PATH}\jre\*
    SetOutPath $INSTDIR\dll\com4j
    File /r ${COMMON_PATH}\dll\com4j\com4j-x86.dll
    SetOutPath $INSTDIR\plugins
    File /r ${PRODUCT_PATH}\plugins\*
    SetOutPath $INSTDIR\readme
    File /r ${PRODUCT_PATH}\readme\*
    SetOutPath $INSTDIR\readme
    File /r ${PRODUCT_PATH}\readme\*
    SetOutPath $INSTDIR
    File /r ${PRODUCT_PATH}\.eclipseproduct
    File /r ${PRODUCT_PATH}\admin.exe
    File /r ${PRODUCT_PATH}\admin.ini
    File /r ${PRODUCT_PATH}\artifacts.xml
    File /r ${COMMON_PATH}\client.exe
    File /r ${COMMON_PATH}\client.ini
    File /r ${PRODUCT_PATH}\epl-v10.html
;    File /r ${PRODUCT_PATH}\launcher.exe
    File /r ${PRODUCT_PATH}\notice.html
    File /r ${COMMON_PATH}\report.exe
    File /r ${COMMON_PATH}\report.ini
    WriteRegStr HKLM "${REGKEY}\Components" Main 1
SectionEnd

Section -post SEC0001
    Exec 'regsvr32.exe /s "$INSTDIR\dll\com4j\com4j-x86.dll"'

    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
;    SetOutPath "$INSTDIR"
    CreateShortCut "$DESKTOP\Administrator.lnk" "$INSTDIR\admin.exe" ""
    CreateShortCut "$DESKTOP\Kasse.lnk" "$INSTDIR\client.exe" ""
    CreateShortCut "$DESKTOP\Auswertungen.lnk" "$INSTDIR\report.exe" ""
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Administrator.lnk" $INSTDIR\admin.exe
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Kasse.lnk" $INSTDIR\client.exe
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Auswertungen.lnk" $INSTDIR\report.exe
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$(^UninstallLink).lnk" $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_END
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
SectionEnd

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

# Uninstaller sections
Section /o -un.Main UNSEC0000
    Exec 'regsvr32.exe /s /u "$INSTDIR\dll\com4j\com4j-x86.dll"'

    RmDir /r /REBOOTOK $INSTDIR\configuration
    RmDir /r /REBOOTOK $INSTDIR\dll
    RmDir /r /REBOOTOK $INSTDIR\features
    RmDir /r /REBOOTOK $INSTDIR\p2
    RmDir /r /REBOOTOK $INSTDIR\jre
    RmDir /r /REBOOTOK $INSTDIR\plugins
    RmDir /r /REBOOTOK $INSTDIR\readme
    Delete /REBOOTOK $INSTDIR\.eclipseproduct
    Delete /REBOOTOK $INSTDIR\admin.exe
    Delete /REBOOTOK $INSTDIR\admin.ini
    Delete /REBOOTOK $INSTDIR\artifacts.xml
    Delete /REBOOTOK $INSTDIR\client.exe
    Delete /REBOOTOK $INSTDIR\client.ini
    Delete /REBOOTOK $INSTDIR\epl-v10.html
    Delete /REBOOTOK $INSTDIR\launcher.exe
    Delete /REBOOTOK $INSTDIR\notice.html
    Delete /REBOOTOK $INSTDIR\report.exe
    Delete /REBOOTOK $INSTDIR\report.ini
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section -un.post UNSEC0001
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
#    Delete "$DESKTOP\Administrator.lnk" "$INSTDIR\admin.exe"
#    Delete "$DESKTOP\Kasse.lnk" "$INSTDIR\client.exe"
#    Delete "$DESKTOP\Auswertungen.lnk" "$INSTDIR\report.exe"
    Delete "$SMPROGRAMS\$StartMenuGroup\Administrator.lnk"
    Delete "$SMPROGRAMS\$StartMenuGroup\Kasse.lnk"
    Delete "$SMPROGRAMS\$StartMenuGroup\Auswertungen.lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^UninstallLink).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /REBOOTOK $INSTDIR
    Push $R0
    StrCpy $R0 $StartMenuGroup 1
    StrCmp $R0 ">" no_smgroup
no_smgroup:
    Pop $R0
SectionEnd

# Installer functions
Function .onInit
    InitPluginsDir
    !insertmacro MULTIUSER_INIT
FunctionEnd

# Uninstaller functions
Function un.onInit
    !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuGroup
    !insertmacro MULTIUSER_UNINIT
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
FunctionEnd

# Installer Language Strings
# TODO Update the Language Strings with the appropriate translations.

LangString ^UninstallLink ${LANG_GERMAN} "Uninstall $(^Name)"
LangString ^UninstallLink ${LANG_ENGLISH} "Uninstall $(^Name)"
