#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_john_example_jp_kotlinproject_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
