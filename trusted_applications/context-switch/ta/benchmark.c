/*
 *
 * Thesis: TWallet - ARM TrustZone Enabled Trustable Mobile Wallet: A Case for Cryptocurrency Wallets
 * NOVA School of Science and Technology, NOVA University of Lisbon
 *
 * Student: Rafael Rodrigues Gameiro nº50677
 * Advisor: Henrique João Domingues
 *
 */

#include <string.h>
#include <string_ext.h>
#include <tee_internal_api.h>
#include <tee_internal_api_extensions.h>

#include <context_switch_benchmark_ta.h>

/*
 * Computes the time different between two registered time values
 * Returns the result in millis
 */
static uint32_t time_diff(TEE_Time *time0, TEE_Time *time1)
{
    return (time1->seconds - time0->seconds) * 1000 + (time1->millis - time0->millis);
}

/*
 * Generates a new RSA keypair to be stored persistently in Secure Storage
 */
static TEE_Result generate_rsakeys(void)
{
    TEE_Result res;
    TEE_ObjectHandle key_pair;

    IMSG("Allocating object\n");
    res = TEE_AllocateTransientObject(TEE_TYPE_RSA_KEYPAIR, KEY_SIZE, &key_pair);
    if (res) {
	EMSG("TEE_AllocateTransientObject(%#" PRIx32 ", %" PRId32 "): %#" PRIx32, TEE_TYPE_RSA_KEYPAIR, KEY_SIZE, res);
	return res;
    }
    
    IMSG("Generate key\n");
    res = TEE_GenerateKey(key_pair, KEY_SIZE, NULL, 0);
    if (res) {
	EMSG("TEE_GenerateKey(%" PRId32 "): %#" PRIx32, KEY_SIZE, res);
	TEE_FreeTransientObject(key_pair);
	return res;
    }

    TEE_FreeTransientObject(key_pair);
    return TEE_SUCCESS;
}

/*
 * Measure the time it takes to perform an heavy operation 
 * without the influnce of the Context Switch
 */
static TEE_Result test_context_switch(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INOUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        TEE_Result res;
        TEE_Time t0 = { };
        TEE_Time t1 = { };

	/*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        TEE_GetREETime(&t0);
        res = generate_rsakeys();
        TEE_GetREETime(&t1);
		
        params[0].value.a = time_diff(&t0, &t1);
        
        return res;
}

/*
 * Called when the instance of the TA is created. This is the first call in
 * the TA.
 */
TEE_Result TA_CreateEntryPoint(void)
{
    /* Nothing to do */	
    
    return TEE_SUCCESS;
}

/*
 * Called when the instance of the TA is destroyed if the TA has not
 * crashed or panicked. This is the last call in the TA.
 */
void TA_DestroyEntryPoint(void)
{
    /* Nothing to do */
}

/*
 * Called when a new session is opened to the TA. *sess_ctx can be updated
 * with a value to be able to identify this session in subsequent calls to the
 * TA. In this function you will normally do the global initialization for the
 * TA.
 */
TEE_Result TA_OpenSessionEntryPoint(uint32_t __unused param_types,
		TEE_Param __unused params[4], void __unused **sess_ctx)
{
        /*
	 * The DMSG() macro is non-standard, TEE Internal API doesn't
	 * specify any means to logging from a TA.
	 */
	IMSG("Service started!\n");

	return TEE_SUCCESS;
}

/*
 * Called when a session is closed, sess_ctx hold the value that was
 * assigned by TA_OpenSessionEntryPoint().
 */
void TA_CloseSessionEntryPoint(void __unused *sess_ctx)
{ 
	IMSG("Goodbye!\n");
}

/*
 * Called when a TA is invoked. sess_ctx hold that value that was
 * assigned by TA_OpenSessionEntryPoint(). The rest of the paramters
 * comes from normal world.
 */
TEE_Result TA_InvokeCommandEntryPoint(void __unused *sess_ctx, uint32_t command, uint32_t param_types, TEE_Param params[4])
{
	switch (command) {
	case B_TEST_CONTEXT_SWITCH:
		return test_context_switch(param_types, params);
	default:
                EMSG("Command ID 0x%x is not supported", command);
                return TEE_ERROR_NOT_SUPPORTED;
	}
}
