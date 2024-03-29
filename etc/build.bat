@echo off
color E

rem Printa a apresentacao do build:
echo.
echo  BUILD DO jLOTHON   [ D:\WORKSPACE\INFINITE\JLOTHON\ETC\BUILD.BAT ]
echo.
echo.

echo  *******************************************
echo  **  INICIANDO  COMPILACAO  DO  jLOTHON.  **
echo  *******************************************
echo.
echo.

echo Posicionando no diretorio raiz do projeto
cd ..
echo.

echo Executando build do projeto com o Maven
call mvn clean compile package
echo.

echo Copiando arquivos e libraries para distribuicao
copy target\*.jar dist\
echo.

echo Efetuando deploy da aplicacao nos projetos Python
copy dist\*.* D:\Workspace\Infinite\Colethon\lib\
copy dist\*.* D:\Workspace\Infinite\Lothon\lib\
echo.

rem Pausa final...
echo.
pause
