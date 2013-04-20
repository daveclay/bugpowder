Install the xuggler jar into the local maven repository
-------------------------------------------------------
```
curl \
   http://xuggle.googlecode.com/svn/trunk/repo/share/java/xuggle/xuggle-xuggler/5.4/xuggle-xuggler-5.4.jar > \
xuggle-xuggler-5.4.jar && \
   mvn install:install-file \
   -Dfile=xuggle-xuggler-5.4.jar \
   -DartifactId=xuggle-xuggler \
   -Dversion=5.4 \
   -DgroupId=xuggle \
   -Dpackaging=jar \
   -DgeneratePom=true
```


Command-line example for the Speech Splitter
--------------------------------------------
```
mvn exec:java \
   -Dexec.mainClass="net.retorx.audio.SpeechSplitter" \
   -Dexec.args="http://gandalf.ddo.jp/mp3/130416.mp3 ohgod."
```
