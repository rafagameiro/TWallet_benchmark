#include <err.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* OP-TEE TEE client API (built by optee_client) */
#include <tee_client_api.h>

/* For the UUID (found in the TA's h-file(s)) */
#include <attestation_ta.h>
#include <attestation_benchmark_ta.h>
#include <benchmark_ta.h>
#include <tee_session.h>
#include <jni.h>

void prepare_tee_session_benchmark(struct test_ctx *ctx, int sign_mode, int key_sz)
{
    TEEC_UUID uuid = TA_ATTESTATION_BENCHMARK_UUID;
    TEEC_Operation op;
    uint32_t err_origin;
    TEEC_Result res;

    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INPUT, TEEC_NONE, TEEC_NONE, TEEC_NONE);

    op.params[0].value.a = sign_mode;
    op.params[0].value.b = key_sz;

    /* Initialize a context connecting us to the TEE */
    res = TEEC_InitializeContext(NULL, &ctx->ctx);
    if (res != TEEC_SUCCESS)
        errx(1, "TEEC_InitializeContext failed with code 0x%x", res);

    /* Opens session to the Authentication Service TA*/
    res = TEEC_OpenSession(&ctx->ctx, &ctx->sess, &uuid, TEEC_LOGIN_PUBLIC, NULL, &op, &err_origin);
    if (res != TEEC_SUCCESS)
        errx(1, "TEEC_Opensession failed with code 0x%x origin 0x%x", res, err_origin);
}

JNIEXPORT jobjectArray
JNICALL Java_com_android_twallet_test_utils_TWalletUtils_attestComponentsTA (JNIEnv * env, jobject obj, jint nonce)
{
    TEEC_UUID uuid = TA_ATTESTATION_UUID;
    struct test_ctx ctx;
    TEEC_Result res;
    TEEC_Operation op;
    uint32_t err_origin;
    char digest[BASE64_MAX_LENGTH];
    char proof[BASE64_MAX_LENGTH];
    char mod[BASE64_MAX_LENGTH];
    char exp[PUBKEY_EXP_LENGTH];

    prepare_tee_session(&ctx, uuid);

    /* Clear the TEEC_Operation struct*/
    memset(&op, 0, sizeof(op));

    /* Specify the argument type*/
    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INOUT, TEEC_MEMREF_TEMP_OUTPUT, TEEC_MEMREF_TEMP_OUTPUT, TEEC_MEMREF_TEMP_OUTPUT);

    op.params[0].value.a = nonce;
    
    op.params[1].tmpref.buffer = digest;
    op.params[1].tmpref.size = sizeof(digest);

    op.params[2].tmpref.buffer = proof;
    op.params[2].tmpref.size = sizeof(proof);

    op.params[3].tmpref.buffer = mod;
    op.params[3].tmpref.size = sizeof(mod);

    jobjectArray values;
    values = (*env)->NewObjectArray(env, 4, (*env)->FindClass(env, "java/lang/String"), NULL);

    res = TEEC_InvokeCommand(&ctx.sess, TA_ATTESTATION_GET_PROOF, &op, &err_origin);
	terminate_tee_session(&ctx);
	
    if (res != TEEC_SUCCESS)
        return values;
    printf("TA successfully attested\n");

    digest[op.params[1].tmpref.size] = '\0';
    proof[op.params[2].tmpref.size] = '\0';
    mod[op.params[3].tmpref.size] = '\0';
    snprintf(exp, PROOF_MAX_LENGTH, "%d", op.params[0].value.b);

    (*env)->SetObjectArrayElement(env, values, 0, (*env)->NewStringUTF(env, digest));
    (*env)->SetObjectArrayElement(env, values, 1, (*env)->NewStringUTF(env, proof));
    (*env)->SetObjectArrayElement(env, values, 2, (*env)->NewStringUTF(env, exp));
    (*env)->SetObjectArrayElement(env, values, 3, (*env)->NewStringUTF(env, mod));

    return values;
}

JNIEXPORT jboolean JNICALL
Java_com_android_twallet_test_utils_TWalletUtils_prepareAttestBenchmarkTA(JNIEnv *env, jclass clazz) {

    struct test_ctx ctx;
    TEEC_Result res;
    TEEC_Operation op;
    uint32_t err_origin;

    /* Useless parameters*/
    prepare_tee_session_benchmark(&ctx, 11, 256);

    /* Clear the TEEC_Operation struct */
    memset(&op, 0, sizeof(op));

    /* Specify the argument type*/
    op.paramTypes = TEEC_PARAM_TYPES(TEEC_NONE, TEEC_NONE, TEEC_NONE, TEEC_NONE);

    res = TEEC_InvokeCommand(&ctx.sess, B_GENERATE_KEYS, &op, &err_origin);
    terminate_tee_session(&ctx);

    if (res != TEEC_SUCCESS)
        return 0;
    printf("TA successfully prepared\n");

    printf("worked!\n");
    return 1; //true
}

JNIEXPORT jobjectArray JNICALL
Java_com_android_twallet_test_utils_TWalletUtils_attestBenchmarkTA(JNIEnv *env, jclass clazz, jint sign_mode, jint key_size, jint nonce) {

    struct test_ctx ctx;
    TEEC_Result res;
    TEEC_Operation op;
    uint32_t err_origin;
    char digest[BASE64_MAX_LENGTH];
    char proof[BASE64_MAX_LENGTH];
    char mod[BASE64_MAX_LENGTH];
    char exp[PUBKEY_EXP_LENGTH];

    prepare_tee_session_benchmark(&ctx, sign_mode, key_size);

    /* Clear the TEEC_Operation struct */
    memset(&op, 0, sizeof(op));

    /* Specify the argument type*/
    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INOUT, TEEC_MEMREF_TEMP_OUTPUT, TEEC_MEMREF_TEMP_OUTPUT, TEEC_MEMREF_TEMP_OUTPUT);

    op.params[0].value.a = nonce;

    op.params[1].tmpref.buffer = digest;
    op.params[1].tmpref.size = sizeof(digest);

    op.params[2].tmpref.buffer = proof;
    op.params[2].tmpref.size = sizeof(proof);

    op.params[3].tmpref.buffer = mod;
    op.params[3].tmpref.size = sizeof(mod);

    jobjectArray values;
    values = (*env)->NewObjectArray(env, 4, (*env)->FindClass(env, "java/lang/String"), NULL);

    res = TEEC_InvokeCommand(&ctx.sess, B_ATTESTATION_GET_PROOF, &op, &err_origin);
    terminate_tee_session(&ctx);

    if (res != TEEC_SUCCESS)
        return values;
    printf("TA successfully attested\n");

    digest[op.params[1].tmpref.size] = '\0';
    proof[op.params[2].tmpref.size] = '\0';
    mod[op.params[3].tmpref.size] = '\0';
    snprintf(exp, PUBKEY_EXP_LENGTH, "%d", op.params[0].value.b);

    (*env)->SetObjectArrayElement(env, values, 0, (*env)->NewStringUTF(env, digest));
    (*env)->SetObjectArrayElement(env, values, 1, (*env)->NewStringUTF(env, proof));
    (*env)->SetObjectArrayElement(env, values, 2, (*env)->NewStringUTF(env, exp));
    (*env)->SetObjectArrayElement(env, values, 3, (*env)->NewStringUTF(env, mod));

    return values;
}

JNIEXPORT jobjectArray JNICALL
Java_com_android_twallet_test_utils_TWalletUtils_attestBenchmarkECTA(JNIEnv *env, jclass clazz, jint sign_mode, jint key_size, jint nonce) {

    struct test_ctx ctx;
    TEEC_Result res;
    TEEC_Operation op;
    uint32_t err_origin;
    char digest[BASE64_MAX_LENGTH];
    char proof[BASE64_MAX_LENGTH];
    char pub_x[EC_PUBLIC_X_LENGTH];
    char pub_y[EC_PUBLIC_Y_LENGTH];

    prepare_tee_session_benchmark(&ctx, sign_mode, key_size);

    /* Clear the TEEC_Operation struct */
    memset(&op, 0, sizeof(op));

    /* Specify the argument type*/
    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INOUT, TEEC_MEMREF_TEMP_OUTPUT, TEEC_MEMREF_TEMP_OUTPUT, TEEC_VALUE_OUTPUT);

    op.params[0].value.a = nonce;

    op.params[1].tmpref.buffer = digest;
    op.params[1].tmpref.size = sizeof(digest);

    op.params[2].tmpref.buffer = proof;
    op.params[2].tmpref.size = sizeof(proof);

    jobjectArray values;
    values = (*env)->NewObjectArray(env, 4, (*env)->FindClass(env, "java/lang/String"), NULL);

    res = TEEC_InvokeCommand(&ctx.sess, B_ATTESTATION_GET_PROOF_EC, &op, &err_origin);
    terminate_tee_session(&ctx);

    if (res != TEEC_SUCCESS)
        return values;
    printf("TA successfully attested\n");

    digest[op.params[1].tmpref.size] = '\0';
    proof[op.params[2].tmpref.size] = '\0';
    snprintf(pub_x, EC_PUBLIC_X_LENGTH, "%d", op.params[3].value.a);
    snprintf(pub_y, EC_PUBLIC_Y_LENGTH, "%d", op.params[3].value.b);

    (*env)->SetObjectArrayElement(env, values, 0, (*env)->NewStringUTF(env, digest));
    (*env)->SetObjectArrayElement(env, values, 1, (*env)->NewStringUTF(env, proof));
    (*env)->SetObjectArrayElement(env, values, 2, (*env)->NewStringUTF(env, pub_x));
    (*env)->SetObjectArrayElement(env, values, 3, (*env)->NewStringUTF(env, pub_y));

    return values;
}

JNIEXPORT jstring JNICALL
Java_com_android_twallet_test_utils_TWalletUtils_keyGenBenchmarkTA(JNIEnv *env, jclass clazz, jint key_type, jint key_size, jint reps) {

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
    op.paramTypes = TEEC_PARAM_TYPES(TEEC_VALUE_INPUT, TEEC_VALUE_INPUT, TEEC_MEMREF_TEMP_OUTPUT, TEEC_NONE);

    /* Gets data contents*/
    op.params[0].value.a = reps;

    op.params[1].value.a = key_type;
    op.params[1].value.b = key_size;

    op.params[2].tmpref.buffer = data;
    op.params[2].tmpref.size = sizeof(data);

    res = TEEC_InvokeCommand(&ctx.sess, B_KEY_GENERATION, &op, &err_origin);
    terminate_tee_session(&ctx);

    if (res != TEEC_SUCCESS)
        return "";

    data[op.params[2].tmpref.size] = '\0';
    return (*env)->NewStringUTF(env, data);

}