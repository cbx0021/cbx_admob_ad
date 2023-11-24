// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("demo");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("demo")
//      }
//    }
#include <jni.h>
#include <string>
extern "C"
JNIEXPORT jstring JNICALL
Java_com_addemo_cbx_1admob_1ad_ad_1manager_ADCBRetrofitClient_getMain(JNIEnv *env, jclass clazz) {
    std::string hello = "https://messagesshop.com/demo-cb-new-2/V1/";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_addemo_cbx_1admob_1ad_ad_1manager_ADCBEasyAES_getKey1(JNIEnv *env, jclass clazz) {
    std::string hello = "qXJWunsBrzSwg5LT";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_addemo_cbx_1admob_1ad_ad_1manager_ADCBEasyAES_getKey2(JNIEnv *env, jclass clazz) {
    std::string hello = "MbjfPoAY8EOinJG5";
    return env->NewStringUTF(hello.c_str());
}