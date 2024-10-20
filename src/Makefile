clean:
	@find . -name "*.class" -type f -delete
	@echo "All .class files removed."

compile: clean
	@javac App.java
	@echo "Compilation finished."

run: compile
	@java App
	@echo "Application finished running."

.PHONY: clean compile run