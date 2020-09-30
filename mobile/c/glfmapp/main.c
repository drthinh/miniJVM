// Example app that draws a triangle. The triangle can be moved via touch or keyboard arrow keys.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "glfm.h"


#define FILE_COMPAT_ANDROID_ACTIVITY glfmAndroidGetActivity()


#include "jvm.h"
#include "jvm_util.h"
#include "media.h"

#ifdef NDEBUG
#define LOG_DEBUG(...) do { } while (0)
#else
#if defined(GLFM_PLATFORM_ANDROID)
#define LOG_DEBUG(...) __android_log_print(ANDROID_LOG_INFO, "GLFM", __VA_ARGS__)
#else
#define LOG_DEBUG(...) do { } while (0)
#endif
#endif

extern void JNI_OnLoad_mini(JniEnv *env);
extern void JNI_OnUnload_mini(JniEnv *env);
static GLFMDisplay *glfm_display;

// Main entry point
void glfmMain(GLFMDisplay *display) {
    glfm_display=display;


     Utf8String *bootclasspath = utf8_create();
    utf8_append_c(bootclasspath, glfmGetResRoot());
    utf8_append_c(bootclasspath, "/resfiles/minijvm_rt.jar;");
    Utf8String *classpath = utf8_create();
    utf8_append_c(classpath, glfmGetResRoot());
    utf8_append_c(classpath, "/resfiles/glfm_gui.jar;");
    //jvm_printf("%s\n",utf8_cstr(classpath));

    jvm_init(utf8_cstr(bootclasspath), utf8_cstr(classpath), JNI_OnLoad_mini);
    sys_properties_set_c("glfm.res.root",glfmGetResRoot());
    sys_properties_set_c("glfm.save.root", glfmGetSaveRoot());
    sys_properties_set_c("glfm.uuid", glfmGetUUID());
    Runtime *runtime=getRuntimeCurThread(&jnienv);

    utf8_destory(classpath);
    utf8_destory(bootclasspath);
    c8* p_classname="org/mini/glfm/GlfmCallBackImpl";
    c8* p_methodname="glinit";
    c8* p_methodtype="(J)V";
    push_long(runtime->stack,(intptr_t)display);
    call_method(p_classname,p_methodname,p_methodtype,runtime);
}

void glfmDestroy(){
    jvm_destroy(JNI_OnUnload_mini);
}
