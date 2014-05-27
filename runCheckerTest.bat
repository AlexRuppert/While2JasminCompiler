@echo off
echo "+++++++++++"
echo "VALID TESTS"
echo "+++++++++++"
for /f "usebackq delims=|" %%f in (`dir /b "CheckerTestFiles\valid"`) do echo %%f && java -cp bin/guava-11.0.2.jar;bin Main "CheckerTestFiles\valid\\"%%f && echo. && echo.

echo "-------------"
echo "INVALID TESTS"
echo "-------------"
for /f "usebackq delims=|" %%f in (`dir /b "CheckerTestFiles\invalid"`) do echo %%f && java -cp bin/guava-11.0.2.jar;bin Main "CheckerTestFiles\invalid\\"%%f && echo. && echo.