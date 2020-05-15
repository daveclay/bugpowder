Command-line example for the Speech Splitter
--------------------------------------------

This example splits the audio stream found at http://gandalf.ddo.jp/mp3/130416.mp3 whenever it finds 0.125 seconds of silence, where silence is defined as a series of samples with an amplitude less than 3.5% of the maximum.

Each output file will be named ohgod.XXXXXXXX.mp3, where XXXXXXXX is a random hex number.

If -a is not specified, 3.5% max amplitude is the silence threshold.

If -t is not specified, 0.125s is the silence duration that triggers a clip.

If -o is not specified, the output file base name is derived from the input filename/URL.

```
mvn exec:java \
   -Dexec.mainClass="net.retorx.audio.SpeechSplitter" \
   -Dexec.args="-a 0.035 -t 0.125 -o ohgod. http://gandalf.ddo.jp/mp3/130416.mp3"
```
