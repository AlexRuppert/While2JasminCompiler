@echo off
echo "+++++++++++"
echo "VALID TESTS"
echo "+++++++++++"
for /f "usebackq delims=|" %%f in (`dir /b "LexerTestFiles\valid"`) do echo %%f && java -cp bin/guava-11.0.2.jar;bin Main "LexerTestFiles\valid\\"%%f && echo. && echo.

echo "-------------"
echo "INVALID TESTS"
echo "-------------"
for /f "usebackq delims=|" %%f in (`dir /b "LexerTestFiles\invalid"`) do echo %%f && java -cp bin/guava-11.0.2.jar;bin Main "LexerTestFiles\invalid\\"%%f && echo. && echo.