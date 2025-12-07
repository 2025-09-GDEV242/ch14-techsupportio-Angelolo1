# Ch12-tech-support-io
 
-----------------------
Changes and Refactoring
-----------------------

1) Multiline Default Responses (fillDefaultResponses)
    > Updated to read default.txt with multiline entries
    > A blank line marks the end of a response
    > Added a checked exception (MultipleBlankLinesException) to 
      detect two or more consectutive blank lines
    > The constructor no catched this exception and provides a fallback
2) Dynamic Response Map (fillResponseMap)
    > Refactored put() statements to read from responses.txt
    > File format
        - First line: comma-separated keys
        - Following lines: the response text
        - Blank line: indicated the end of the key-response entry
    > All keys on the first line are mapped to the same response
    > Multiline responses are concatenated into a single string