curl http://xuggle.googlecode.com/svn/trunk/repo/share/java/xuggle/xuggle-xuggler/5.4/xuggle-xuggler-5.4.jar > xuggle-xuggler-5.4.jar
mvn install:install-file -Dfile=xuggle-xuggler-5.4.jar -DartifactId=xuggle-xuggler -Dversion=5.4 -DgroupId=xuggle -Dpackaging=jar -DgeneratePom=true
