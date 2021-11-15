#include <err.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <inttypes.h>
#include <jni.h>
#include <tee_session.h>

/* For the UUID (found in the TA's h-file(s)) */
#include <context_switch_benchmark_ta.h>

JNIEXPORT jint JNICALL
Java_com_android_twallet_test_utils_TWalletUtils_contextSwitchBenchmarkTA(JNIEnv *env,
                                                                          jclass clazz) {

    TEEC_UUID uuid = TA_CONTEXT_SWITCH_UUID;
    struct test_ctx ctx;
    TEEC_Result res;
    TEEC_Operation op;
    uint32_t err_origin;

    /* Clear the TEEC_Operation struct */
    memset(&op, 0, sizeof(op));

    /* Specify the argument type*/
    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INOUT, TEEC_NONE, TEEC_NONE, TEEC_NONE);

    op.params[0].value.a = 0;

    prepare_tee_session(&ctx, uuid);

    res = TEEC_InvokeCommand(&ctx.sess, B_TEST_CONTEXT_SWITCH, &op, &err_origin);

    terminate_tee_session(&ctx);

    if (res != TEEC_SUCCESS)
        errx(1, "TEEC_InvokeCommand failed with code 0x%x origin 0x%x", res, err_origin);
    printf("TA successfully attestated\n");

    return op.params[0].value.a;
}