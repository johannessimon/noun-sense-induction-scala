assembly:
	./sbt/sbt 'set test in assembly := {}' assembly

clean:
	./sbt/sbt clean 

clean-assembly:
	./sbt/sbt 'set test in assembly := {}' clean assembly

clean-test-assembly:
	./sbt/sbt clean assembly

test:
	./sbt/sbt "testOnly -- -l NeedsMissingFiles"
