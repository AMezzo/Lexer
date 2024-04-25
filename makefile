# Defining some variables to prevent typos
COMPILE_DIR=target
SRC_DIR=.
SOURCE_FILE=sources
JUNIT_JAR=lib/junit-platform-console-standalone-1.9.3.jar
ENV_FORMAT="| %-20s | %-47s |\n"
TARBALL=assignment-1-tests.tar
ASSIGNMENT_GRADER=https://raw.githubusercontent.com/sfsu-csc-413-fall-2023/grader-lexical-analysis/main/

# Note: Anywhere you see @, that means I want to prevent that 
#     command from printing (and only want to see the result of
#     executing that command)

# Little utility to print out my variables
env:
	@echo "Current environment configuration:"
	@head -c 74 < /dev/zero | tr '\0' '-'
	@echo
	@printf $(ENV_FORMAT) "COMPILE_DIR" $(COMPILE_DIR)
	@printf $(ENV_FORMAT) "SRC_DIR" $(SRC_DIR)
	@printf $(ENV_FORMAT) "SOURCE_FILE" $(SOURCE_FILE)
	@printf $(ENV_FORMAT) "JUNIT_JAR" $(JUNIT_JAR)
	@head -c 74 < /dev/zero | tr '\0' '-'
	@echo

# clean should be used to remove all compiled (.class) files
# the find command on *nix finds all files (-type f) starting from the 
#    current directory (.), where the filename ends in ".class", and
#    deletes (-delete) them
clean:
	@echo "Deleting all class files..."
	@find . -name "*.class" -type f -delete
	@echo "Deleting compiled files..."
	@rm -rf $(COMPILE_DIR)
	@rm -f $(SOURCE_FILE)
	@rm -f $(TARBALL)

# compile depends on clean to ensure we start from a clean slate
#     every time, so that any changes we make to our java files are 
#     re-compiled
# the find command then looks for all files recursively from the 
#     src directory (./src), with the extension ".java"
# this file listing is then output into a file named "sources.txt",
#     which is used as an input to the java compiler (javac)
# the javac command then compiles the files listed in "sources.txt" 
#     into a directory (-d) named "target" (just to keep the .class 
#     files out of our nicely organized packages)
# finally, we remove the temporary "sources.txt" file
all: clean
	@echo "Compiling..."
	@find $(SRC_DIR) -name "*.java" -not -path "./tests/*" > $(SOURCE_FILE)
	@javac -d $(COMPILE_DIR) @$(SOURCE_FILE)
	@rm $(SOURCE_FILE)

# Pretty much the same as compile, but we want to include the tests
#     and their dependencies (the junit jar)
# find adds all files with the ".java" extension from the current 
#     directory (instead of the ./src directory) to the "sources.txt"
#     file. could I have used the compile directive? sure, but I'm
#     lazy, and this was easier for me
# since there are some additional dependencies, we need to set the 
#     classpath (-cp) for the java compiler to include those dependencies
# then we clean up the temporary "sources.txt" file, as before
all-tests: clean
	@echo "Compiling for testing..."
	@find . -name "*.java" > $(SOURCE_FILE)
	@javac -d $(COMPILE_DIR) -cp $(COMPILE_DIR):$(JUNIT_JAR):. @$(SOURCE_FILE)
	@rm $(SOURCE_FILE)

# Depends on all-tests to get the tests compiled as well as
#     the src directory
# When we run, we want to run the junit jar in order to run the tests,
#     but we need to tell java that the classpath that should be used
#     is the target directory. --scan-classpath just looks in the classpath
#     for _any_ tests, and executes those
test-all: all-tests
	@echo "Running tests..."
	@java -jar $(JUNIT_JAR) -cp $(COMPILE_DIR) --disable-banner --include-classname=.* --scan-classpath

# we can run individual tests by specifying them using the --select-class flag
# the TEST_NAME environment variable must be the fully qualified class name, 
#     like tests.packageName.TestClassName
# to run a single test, us:
#     make test TEST_NAME=tests.packageName.TestClassName
#     make test METHOD_NAME=tests.packageName.TestClassName#methodName
test: all-tests
	@echo "Running $(TEST_NAME)..."
	@java -jar $(JUNIT_JAR) -cp $(COMPILE_DIR) -c $(TEST_NAME) --disable-banner

test-method: tools all-tests
	@echo "Running $(METHOD_NAME)..."
	@java -jar lib/junit-platform-console-standalone-1.9.3.jar -cp target -m $(METHOD_NAME)

tools: clean
	@echo "Regenerating files..."
	@javac -d $(COMPILE_DIR) lexer/tools/ToolRunner.java
	@java -cp $(COMPILE_DIR) lexer.tools.ToolRunner
	@rm -rf $(COMPILE_DIR)

lexer: clean
	@javac -d $(COMPILE_DIR) lexer/Lexer.java
	@java -cp $(COMPILE_DIR) lexer.Lexer

update-tests: clean
	@rm -rf tests
	@wget -q $(ASSIGNMENT_GRADER)/$(TARBALL)
	@tar -xf $(TARBALL)
	@rm -f $(TARBALL)

package-tests: clean
	@gtar cf $(TARBALL) tests