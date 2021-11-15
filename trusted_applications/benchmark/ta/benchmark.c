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

#include <benchmark_defines.h>

/*
 * Computes the time different between two registered time values
 * Returns the result in millis
 */
static uint32_t time_diff(TEE_Time *time0, TEE_Time *time1)
{
    return (time1->seconds * 1000 + time1->millis) - (time0->seconds * 1000 + time0->millis);
}

/*
 * Sends to the Normal World the values registered during the test execution
 */
static TEE_Result export_data(char* data, uint32_t* data_sz, uint32_t values[], uint32_t values_sz)
{
    
    char field[SEGMENT_MAX_LENGTH];
    strlcpy(data, "{", *data_sz);

    strlcpy(field, "\"length\":", SEGMENT_MAX_LENGTH);    
    strlcat(data, field, *data_sz);
    
    snprintf(field, SEGMENT_MAX_LENGTH, "%" PRIu32, values_sz);  
    strlcat(field, ",", SEGMENT_MAX_LENGTH);
    strlcat(data, field, *data_sz);
    
    strlcpy(field, "\"values\": [", SEGMENT_MAX_LENGTH);
    strlcat(data, field, *data_sz);
    for(uint32_t i = 0; i < values_sz; i++) 
    {
        snprintf(field, SEGMENT_MAX_LENGTH, "%" PRIu32, values[i]);  
        strlcat(data, field, *data_sz);

        if(i + 1 < values_sz)
            strlcat(data, ",", *data_sz);
    }

    strlcat(data, "]}", *data_sz);
    *data_sz = strlen(data);

    return TEE_SUCCESS;
}

/*
 * Generates a new RSA keypair to be stored persistently in Secure Storage
 */
static TEE_Result generate_rsakeys(uint32_t key_sz)
{
    TEE_Result res;
    TEE_ObjectHandle key_pair;

    res = TEE_AllocateTransientObject(TEE_TYPE_RSA_KEYPAIR, key_sz, &key_pair);
    if (res) {
	EMSG("TEE_AllocateTransientObject(%#" PRIx32 ", %" PRId32 "): %#" PRIx32, TEE_TYPE_RSA_KEYPAIR, key_sz, res);
	return res;
    }
    
    IMSG("Generating key"); 
    res = TEE_GenerateKey(key_pair, key_sz, NULL, 0);
    if (res) {
	EMSG("TEE_GenerateKey(%" PRId32 "): %#" PRIx32, key_sz, res);
	TEE_FreeTransientObject(key_pair);
	return res;
    }
    
    return TEE_SUCCESS;
}

/*
 * Generates a new ECDSA keypair to be stored persistently in Secure Storage
 */
static TEE_Result generate_eckeys(uint32_t key_sz)
{
    TEE_Result res;
    TEE_ObjectHandle key_pair;

    res = TEE_AllocateTransientObject(TEE_TYPE_ECDSA_KEYPAIR, key_sz, &key_pair);
    if (res) {
	EMSG("TEE_AllocateTransientObject(%#" PRIx32 ", %" PRId32 "): %#" PRIx32, TEE_TYPE_ECDSA_KEYPAIR, key_sz, res);
	return res;
    }
    
    TEE_Attribute ec_attr;
    
    ec_attr.attributeID = TEE_ATTR_ECC_CURVE;
    ec_attr.content.value.a = TEE_ECC_CURVE_NIST_P256;
    ec_attr.content.value.b = 32;
   
    IMSG("Generating key"); 
    res = TEE_GenerateKey(key_pair, key_sz, &ec_attr, 1);
    if (res) {
	EMSG("TEE_GenerateKey(%" PRId32 "): %#" PRIx32, key_sz, res);
	TEE_FreeTransientObject(key_pair);
	return res;
    }

    return TEE_SUCCESS;
}

/*
 * Measures the time it takes to generate a set of RSA keypairs
 */
static TEE_Result test_key_generation_rsa(uint32_t values[], uint32_t values_sz, uint32_t key_sz)
{
    
    TEE_Result res;    
    TEE_Time t0 = { };
    TEE_Time t1 = { };

        
    for(uint32_t i = 0; i < values_sz; i++)
    {
        TEE_GetSystemTime(&t0);
        
        res = generate_rsakeys(key_sz);
	if (res != TEE_SUCCESS)
		return res;
        TEE_GetSystemTime(&t1);
        values[i] = time_diff(&t0, &t1);
    }

    return TEE_SUCCESS;
}

/*
 * Measures the time it takes to generate a set of EC keypairs
 */
static TEE_Result test_key_generation_ec(uint32_t values[], uint32_t values_sz, uint32_t key_sz)
{
    
    TEE_Result res;    
    TEE_Time t0 = { };
    TEE_Time t1 = { };

        
    for(uint32_t i = 0; i < values_sz; i++)
    {
        TEE_GetSystemTime(&t0);
        
        res = generate_eckeys(key_sz);
	if (res != TEE_SUCCESS)
		return res;
        TEE_GetSystemTime(&t1);
        values[i] = time_diff(&t0, &t1);
    }

    return TEE_SUCCESS;
}

/*
 * Measures the time it takes to generates a set of keypairs
 * The type of generated keys can be either RSA or ECDSA
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result key_generation(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE);

	TEE_Result res = TEE_ERROR_GENERIC;
	char* data;
        uint32_t data_sz;
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];

        uint32_t key_type = params[1].value.a;
        uint32_t key_sz = params[1].value.b;
           
        if (key_type == 1)
            res = test_key_generation_rsa(values, len, key_sz);
        else
            res = test_key_generation_ec(values, len, key_sz);

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[2].memref.buffer, data, data_sz);
	params[2].memref.size = data_sz;

        TEE_Free(data);
        return res;
}

/*
 * Measures the time it takes to store a set of credentials
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result store_credentials(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        const TEE_UUID system_uuid = TA_ADAPTATION_LAYER_UUID;
	TEE_TASessionHandle session = TEE_HANDLE_NULL;
	TEE_Result res = TEE_ERROR_GENERIC;
	uint32_t ret_origin = 0;
	char* data;
        uint32_t data_sz;
        char obj_id[OBJ_LENGTH];
        char credentials[CREDENTIALS_LENGTH];
        TEE_Time t0 = { };
        TEE_Time t1 = { };
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];

            
        const uint32_t param_types_test = TEE_PARAM_TYPES( TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE);

	TEE_Param params_test[TEE_NUM_PARAMS] = { 0 };
        
        char i_val[10];
        for(uint32_t i = 0; i < len; i++)
        {
            TEE_GetSystemTime(&t0);
            IMSG("Opening session");
	    res = TEE_OpenTASession(&system_uuid, TEE_TIMEOUT_INFINITE,
				0, NULL, &session, &ret_origin);
	    if (res != TEE_SUCCESS)
		return res;
	  
             
            snprintf(i_val, 10, "%d", i);
            strlcpy(obj_id, "account_", OBJ_LENGTH);    
            strlcat(obj_id, i_val, OBJ_LENGTH);
            params_test[0].memref.buffer = obj_id;
	    params_test[0].memref.size = strlen(obj_id);
            
            strlcpy(credentials, "user_", CREDENTIALS_LENGTH);    
            strlcat(credentials, i_val, CREDENTIALS_LENGTH);
            strlcat(credentials, "|pass", CREDENTIALS_LENGTH);
            params_test[1].memref.buffer = credentials;
	    params_test[1].memref.size = strlen(credentials);

            IMSG("Invoking command");
	    res = TEE_InvokeTACommand(session, TEE_TIMEOUT_INFINITE,
				  AL_STORE_CREDENTIALS, param_types_test, params_test, &ret_origin);

	    TEE_CloseTASession(session);

	    if (res != TEE_SUCCESS)
		    return res;
            TEE_GetSystemTime(&t1);
            values[i] = time_diff(&t0, &t1);
        }

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[1].memref.buffer, data, data_sz);
	params[1].memref.size = data_sz;

        TEE_Free(data);
        return res;
}

/*
 * Measures the time it takes to load a set of credentials
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result load_credentials(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        const TEE_UUID system_uuid = TA_ADAPTATION_LAYER_UUID;
	TEE_TASessionHandle session = TEE_HANDLE_NULL;
	TEE_Result res = TEE_ERROR_GENERIC;
	uint32_t ret_origin = 0;
	char* data;
        uint32_t data_sz;
        char obj_id[OBJ_LENGTH];
        char credentials[CREDENTIALS_LENGTH];
        TEE_Time t0 = { };
        TEE_Time t1 = { };
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];

            
        const uint32_t param_types_test = TEE_PARAM_TYPES( TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_MEMREF_OUTPUT,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE);

	TEE_Param params_test[TEE_NUM_PARAMS] = { 0 };
        
        char i_val[10];
        for(uint32_t i = 0; i < len; i++)
        {
            TEE_GetSystemTime(&t0);
            IMSG("Opening session");
	    res = TEE_OpenTASession(&system_uuid, TEE_TIMEOUT_INFINITE,
				0, NULL, &session, &ret_origin);
	    if (res != TEE_SUCCESS)
		return res;
	  
             
            snprintf(i_val, 10, "%d", i);
            strlcpy(obj_id, "account_", OBJ_LENGTH);    
            strlcat(obj_id, i_val, OBJ_LENGTH);
            params_test[0].memref.buffer = obj_id;
	    params_test[0].memref.size = strlen(obj_id);
            
            params_test[1].memref.buffer = credentials;
	    params_test[1].memref.size = sizeof(credentials);

            IMSG("Invoking command");
	    res = TEE_InvokeTACommand(session, TEE_TIMEOUT_INFINITE,
				  AL_LOAD_CREDENTIALS, param_types_test, params_test, &ret_origin);

	    TEE_CloseTASession(session);

	    if (res != TEE_SUCCESS)
		    return res;
            TEE_GetSystemTime(&t1);
            values[i] = time_diff(&t0, &t1);
        }

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[1].memref.buffer, data, data_sz);
	params[1].memref.size = data_sz;

        TEE_Free(data);
        return res;
}

/*
 * Measures the time it takes to delete a set of credentials
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result delete_credentials(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        const TEE_UUID system_uuid = TA_ADAPTATION_LAYER_UUID;
	TEE_TASessionHandle session = TEE_HANDLE_NULL;
	TEE_Result res = TEE_ERROR_GENERIC;
	uint32_t ret_origin = 0;
	char* data;
        uint32_t data_sz;
        char obj_id[OBJ_LENGTH];
        TEE_Time t0 = { };
        TEE_Time t1 = { };
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];

            
        const uint32_t param_types_test = TEE_PARAM_TYPES( TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE);

	TEE_Param params_test[TEE_NUM_PARAMS] = { 0 };
        
        char i_val[10];
        for(uint32_t i = 0; i < len; i++)
        {
            TEE_GetSystemTime(&t0);
            IMSG("Opening session");
	    res = TEE_OpenTASession(&system_uuid, TEE_TIMEOUT_INFINITE,
				0, NULL, &session, &ret_origin);
	    if (res != TEE_SUCCESS)
		return res;
	  
             
            snprintf(i_val, 10, "%d", i);
            strlcpy(obj_id, "account_", OBJ_LENGTH);    
            strlcat(obj_id, i_val, OBJ_LENGTH);
            params_test[0].memref.buffer = obj_id;
	    params_test[0].memref.size = strlen(obj_id);
            
            IMSG("Invoking command");
	    res = TEE_InvokeTACommand(session, TEE_TIMEOUT_INFINITE,
				  AL_DELETE_CREDENTIALS, param_types_test, params_test, &ret_origin);

	    TEE_CloseTASession(session);

	    if (res != TEE_SUCCESS)
		    return res;
            TEE_GetSystemTime(&t1);
            values[i] = time_diff(&t0, &t1);
        }

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[1].memref.buffer, data, data_sz);
	params[1].memref.size = data_sz;

        TEE_Free(data);
        return res;
}

/*
 * Measures the time it takes to write a set portions of data
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result storage_write_data(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        const TEE_UUID system_uuid = TA_ADAPTATION_LAYER_UUID;
	TEE_TASessionHandle session = TEE_HANDLE_NULL;
	TEE_Result res = TEE_ERROR_GENERIC;
	uint32_t ret_origin = 0;
	char* data;
        uint32_t data_sz;
        char obj_id[OBJ_LENGTH];
        char account[DATA_LENGTH];
        TEE_Time t0 = { };
        TEE_Time t1 = { };
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];

            
        const uint32_t param_types_test = TEE_PARAM_TYPES( TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE);

	TEE_Param params_test[TEE_NUM_PARAMS] = { 0 };
        
        char i_val[10];
        for(uint32_t i = 0; i < len; i++)
        {
            TEE_GetSystemTime(&t0);
            IMSG("Opening session");
	    res = TEE_OpenTASession(&system_uuid, TEE_TIMEOUT_INFINITE,
				0, NULL, &session, &ret_origin);
	    if (res != TEE_SUCCESS)
		return res;
	  
             
            snprintf(i_val, 10, "%d", i);
            strlcpy(obj_id, "account_", OBJ_LENGTH);    
            strlcat(obj_id, i_val, OBJ_LENGTH);
            params_test[0].memref.buffer = obj_id;
	    params_test[0].memref.size = strlen(obj_id);
            
            strlcpy(account, DATA_EXAMPLE, DATA_LENGTH);    
            params_test[1].memref.buffer = account;
	    params_test[1].memref.size = strlen(account);
            
            IMSG("Invoking command");
	    res = TEE_InvokeTACommand(session, TEE_TIMEOUT_INFINITE,
				  AL_STORAGE_WRITE_DATA, param_types_test, params_test, &ret_origin);

	    TEE_CloseTASession(session);

	    if (res != TEE_SUCCESS)
		    return res;
            TEE_GetSystemTime(&t1);
            values[i] = time_diff(&t0, &t1);
        }

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[1].memref.buffer, data, data_sz);
	params[1].memref.size = data_sz;

        TEE_Free(data);
        return res;
}

/*
 * Measures the time it takes to retrieve a set portions of data
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result storage_read_data(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        const TEE_UUID system_uuid = TA_ADAPTATION_LAYER_UUID;
	TEE_TASessionHandle session = TEE_HANDLE_NULL;
	TEE_Result res = TEE_ERROR_GENERIC;
	uint32_t ret_origin = 0;
	char* data;
        uint32_t data_sz;
        char obj_id[OBJ_LENGTH];
        char account[DATA_LENGTH];
        TEE_Time t0 = { };
        TEE_Time t1 = { };
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];

            
        const uint32_t param_types_test = TEE_PARAM_TYPES( TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_MEMREF_OUTPUT,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE);

	TEE_Param params_test[TEE_NUM_PARAMS] = { 0 };
        
        char i_val[10];
        for(uint32_t i = 0; i < len; i++)
        {
            TEE_GetSystemTime(&t0);
            IMSG("Opening session");
	    res = TEE_OpenTASession(&system_uuid, TEE_TIMEOUT_INFINITE,
				0, NULL, &session, &ret_origin);
	    if (res != TEE_SUCCESS)
		return res;
	  
             
            snprintf(i_val, 10, "%d", i);
            strlcpy(obj_id, "account_", OBJ_LENGTH);    
            strlcat(obj_id, i_val, OBJ_LENGTH);
            params_test[0].memref.buffer = obj_id;
	    params_test[0].memref.size = strlen(obj_id);
           
            params_test[1].memref.buffer = account;
	    params_test[1].memref.size = sizeof(account);

            IMSG("Invoking command");
	    res = TEE_InvokeTACommand(session, TEE_TIMEOUT_INFINITE,
				  AL_STORAGE_READ_DATA, param_types_test, params_test, &ret_origin);

	    TEE_CloseTASession(session);

	    if (res != TEE_SUCCESS)
		    return res;
            TEE_GetSystemTime(&t1);
            values[i] = time_diff(&t0, &t1);
        }

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[1].memref.buffer, data, data_sz);
	params[1].memref.size = data_sz;

        TEE_Free(data);
        return res;
}

/*
 * Measures the time it takes to delete a set portions of data
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result storage_delete_data(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        const TEE_UUID system_uuid = TA_ADAPTATION_LAYER_UUID;
	TEE_TASessionHandle session = TEE_HANDLE_NULL;
	TEE_Result res = TEE_ERROR_GENERIC;
	uint32_t ret_origin = 0;
	char* data;
        uint32_t data_sz;
        char obj_id[OBJ_LENGTH];
        TEE_Time t0 = { };
        TEE_Time t1 = { };
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];

            
        const uint32_t param_types_test = TEE_PARAM_TYPES( TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE);

	TEE_Param params_test[TEE_NUM_PARAMS] = { 0 };
        
        char i_val[10];
        for(uint32_t i = 0; i < len; i++)
        {
            TEE_GetSystemTime(&t0);
            IMSG("Opening session");
	    res = TEE_OpenTASession(&system_uuid, TEE_TIMEOUT_INFINITE,
				0, NULL, &session, &ret_origin);
	    if (res != TEE_SUCCESS)
		return res;
	  
             
            snprintf(i_val, 10, "%d", i);
            strlcpy(obj_id, "account_", OBJ_LENGTH);    
            strlcat(obj_id, i_val, OBJ_LENGTH);
            params_test[0].memref.buffer = obj_id;
	    params_test[0].memref.size = strlen(obj_id);
           
            IMSG("Invoking command");
	    res = TEE_InvokeTACommand(session, TEE_TIMEOUT_INFINITE,
				  AL_STORAGE_DELETE_DATA, param_types_test, params_test, &ret_origin);

	    TEE_CloseTASession(session);

	    if (res != TEE_SUCCESS)
		    return res;
            TEE_GetSystemTime(&t1);
            values[i] = time_diff(&t0, &t1);
        }

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[1].memref.buffer, data, data_sz);
	params[1].memref.size = data_sz;

        TEE_Free(data);
        return res;
}

/*
 * Measures the time it takes to register a new entry into the Logging Service
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result log_new_entry(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        const TEE_UUID system_uuid = TA_LOGGING_UUID;
	TEE_TASessionHandle session = TEE_HANDLE_NULL;
	TEE_Result res = TEE_ERROR_GENERIC;
	uint32_t ret_origin = 0;
	char* data;
        uint32_t data_sz;
        const char* service = "AUTH_SERVICE";
        const char* command = "STORE_CREDENTIALS";
        TEE_Time t0 = { };
        TEE_Time t1 = { };
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];
        
        const uint32_t param_types_test = TEE_PARAM_TYPES( TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_MEMREF_INPUT,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE);

	TEE_Param params_test[TEE_NUM_PARAMS] = { 0 };

        for(uint32_t i = 0; i < len; i++)
        {
            TEE_GetSystemTime(&t0);
            IMSG("Opening session");
	    res = TEE_OpenTASession(&system_uuid, TEE_TIMEOUT_INFINITE,
				0, NULL, &session, &ret_origin);
	    if (res != TEE_SUCCESS)
		return res;
            
            params_test[0].memref.buffer = service;
	    params_test[0].memref.size = strlen(service);
            
            params_test[1].memref.buffer = command;
	    params_test[1].memref.size = strlen(command);

            IMSG("Invoking command");
	    res = TEE_InvokeTACommand(session, TEE_TIMEOUT_INFINITE,
				  TA_LOGGING_NEW_ENTRY, param_types_test, params_test, &ret_origin);

	    TEE_CloseTASession(session);

	    if (res != TEE_SUCCESS)
		    return res;
            TEE_GetSystemTime(&t1);
            values[i] = time_diff(&t0, &t1);
        }

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[1].memref.buffer, data, data_sz);
	params[1].memref.size = data_sz;
        
        TEE_Free(data);
        return res;
}

/*
 * Measures the time it takes to filter an operation with the Monitoring Service
 * After obtaining the times, the results are compressed 
 * and send back to the Normal World
 */
static TEE_Result filter_operation(uint32_t param_types, TEE_Param params[4])
{
	const uint32_t exp_param_types = TEE_PARAM_TYPES(TEE_PARAM_TYPE_VALUE_INPUT,
				                        TEE_PARAM_TYPE_MEMREF_OUTPUT,
				                        TEE_PARAM_TYPE_NONE,
				                        TEE_PARAM_TYPE_NONE);

        const TEE_UUID system_uuid = TA_MONITORING_UUID;
	TEE_TASessionHandle session = TEE_HANDLE_NULL;
	TEE_Result res = TEE_ERROR_GENERIC;
	uint32_t ret_origin = 0;
	char* data;
        uint32_t data_sz;
        TEE_Time t0 = { };
        TEE_Time t1 = { };
        
        /*
	 * Safely get the invocation parameters
	 */
	if (param_types != exp_param_types)
		return TEE_ERROR_BAD_PARAMETERS;

        uint32_t len = params[0].value.a;
        uint32_t values[len];
        
        const uint32_t param_types_test = TEE_PARAM_TYPES( TEE_PARAM_TYPE_VALUE_INPUT,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE,
						      TEE_PARAM_TYPE_NONE);

	TEE_Param params_test[TEE_NUM_PARAMS] = { 0 };

        for(uint32_t i = 0; i < len; i++)
        {
            TEE_GetSystemTime(&t0);
            IMSG("Opening session");
	    res = TEE_OpenTASession(&system_uuid, TEE_TIMEOUT_INFINITE,
				0, NULL, &session, &ret_origin);
	    if (res != TEE_SUCCESS)
		return res;
            
            params_test[0].value.a = 1;
	    params_test[0].value.b = 1;
            
            IMSG("Invoking command");
	    res = TEE_InvokeTACommand(session, TEE_TIMEOUT_INFINITE,
				  TA_MONITORING_FILTER_OP, param_types_test, params_test, &ret_origin);

	    TEE_CloseTASession(session);

	    if (res != TEE_SUCCESS)
		    return res;
            TEE_GetSystemTime(&t1);
            values[i] = time_diff(&t0, &t1);
        }

        data_sz = OUTPUT_MAX_LENGTH;
        data = TEE_Malloc(data_sz, 0);
        if(!data)
	    return TEE_ERROR_OUT_OF_MEMORY;

        export_data(data, &data_sz, values, len);
        TEE_MemMove(params[1].memref.buffer, data, data_sz);
	params[1].memref.size = data_sz;

        TEE_Free(data);
        return res;
}

/*
 * Called when the instance of the TA is created. This is the first call in
 * the TA.
 */
TEE_Result TA_CreateEntryPoint(void)
{

    IMSG("Entry Point successfuly created!\n");
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
	case B_KEY_GENERATION:
		return key_generation(param_types, params);
	case B_STORE_CREDENTIALS:
		return store_credentials(param_types, params);
	case B_LOAD_CREDENTIALS:
		return load_credentials(param_types, params);
	case B_DELETE_CREDENTIALS:
		return delete_credentials(param_types, params);
	case B_STORAGE_READ_DATA:
		return storage_read_data(param_types, params);
	case B_STORAGE_WRITE_DATA:
		return storage_write_data(param_types, params);
	case B_STORAGE_DELETE_DATA:
		return storage_delete_data(param_types, params);
	case B_LOGGING_NEW_ENTRY:
		return log_new_entry(param_types, params);
	case B_MONITORING_FILTER:
		return filter_operation(param_types, params);
	default:
                EMSG("Command ID 0x%x is not supported", command);
                return TEE_ERROR_NOT_SUPPORTED;
	}
}
