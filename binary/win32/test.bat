mini_jvm.exe -bootclasspath ../lib/minijvm_rt.jar -cp ../libex/glfw_gui.jar test/AppManagerTest

@echo compile BpDeepTest.java
mini_jvm -bootclasspath ../lib/minijvm_rt.jar -cp ../libex/janino.jar;../libex/commons-compiler.jar org.codehaus.janino.Compiler  ../res/BpDeepTest.java

@echo execute BpDeepTest
mini_jvm -bootclasspath ../lib/minijvm_rt.jar -cp ../res/ BpDeepTest
mini_jvm -bootclasspath ../lib/minijvm_rt.jar -cp ../libex/luaj.jar Sample


pause