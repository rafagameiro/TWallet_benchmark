#include <err.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <tee_session.h>

/* For the UUID (found in the TA's h-file(s)) */
#include <adaptation_layer_ta.h>
#include <benchmark_ta.h>

#define MAX_DATA_SIZE        7000


JNIEXPORT jboolean
JNICALL Java_com_android_twallet_test_utils_TWalletUtils_setTriggerTA(JNIEnv *env, jobject obj,
                                                                        jboolean jbool) {
    TEEC_UUID uuid = TA_ADAPTATION_LAYER_UUID;
    struct test_ctx ctx;
    TEEC_Result res;
    TEEC_Operation op;
    uint32_t err_origin;

    prepare_tee_session(&ctx, uuid);

    /* Clear the TEEC_Operation struct */
    memset(&op, 0, sizeof(op));

    /* Specify the argument type*/
    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INPUT, TEEC_NONE, TEEC_NONE, TEEC_NONE);

    /* Gets data contents*/
    op.params[0].value.a = (jbool == JNI_TRUE);

    res = TEEC_InvokeCommand(&ctx.sess, AL_TRIGGER_MONITORING, &op, &err_origin);
    if (res != TEEC_SUCCESS)
        return false;
    printf("TA successfully read event log\n");

    terminate_tee_session(&ctx);

    return true;
}

JNIEXPORT jstring JNICALL
Java_com_android_twallet_test_utils_TWalletUtils_filterOperationBenchmarkTA(JNIEnv *env, jclass clazz, jint reps) {

    TEEC_UUID uuid = TA_BENCHMARK_UUID;
    struct test_ctx ctx;
    TEEC_Result res;
    TEEC_Operation op;
    uint32_t err_origin;
    char data[OUTPUT_MAX_LENGTH];

    prepare_tee_session(&ctx, uuid);

    /* Clear the TEEC_Operation struct */
    memset(&op, 0, sizeof(op));

    /* Specify the argument type*/
    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INPUT, TEEC_MEMREF_TEMP_OUTPUT, TEEC_NONE, TEEC_NONE);

    /* Gets data contents*/
    op.params[0].value.a = reps;

    op.params[1].tmpref.buffer = data;
    op.params[1].tmpref.size = sizeof(data);

    res = TEEC_InvokeCommand(&ctx.sess, B_MONITORING_FILTER, &op, &err_origin);
    terminate_tee_session(&ctx);

    if (res != TEEC_SUCCESS)
        return "";

    data[op.params[1].tmpref.size] = '\0';
    return (*env)->NewStringUTF(env, data);
}