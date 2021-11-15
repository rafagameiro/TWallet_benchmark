/*
 *
 * Thesis: TWallet - ARM TrustZone Enabled Trustable Mobile Wallet: A Case for Cryptocurrency Wallets
 * NOVA School of Science and Technology, NOVA University of Lisbon
 *
 * Student: Rafael Rodrigues Gameiro nº50677
 * Advisor: Henrique João Domingues
 *
 */
#ifndef TA_ATTESTATION_DEFINES_H
#define TA_ATTESTATION_DEFINES_H

#include <attestation_benchmark_ta.h>
#include <ta_defines.h>

/* Additional Constants used in TA functions*/
#define HASH_MAX_LENGTH                                 100 
#define DIGEST_MAX_LENGTH                               100 
#define PROOF_MAX_LENGTH                                512 
#define PUBKEY_EXP_LENGTH                               20 
#define PUBKEY_MOD_LENGTH                               512 
#define EC_PUBLIC_X_LENGTH                              1000
#define EC_PUBLIC_Y_LENGTH                              1000
#define BASE64_MAX_LENGTH                               1000 

#define ATTESTATION_KEYS_RSA_1024 \
	"ed5a424e-beef-4431-93be-fe8418d9a8b9-RSA-1024"

#define ATTESTATION_KEYS_RSA_2048 \
	"ed5a424e-beef-4431-93be-fe8418d9a8b9-RSA-2048"

#define ATTESTATION_KEYS_RSA_4096 \
	"ed5a424e-beef-4431-93be-fe8418d9a8b9-RSA-4096"

#define ATTESTATION_KEYS_ECDSA \
	"ed5a424e-beef-4431-93be-fe8418d9a8b9-ECDSA"

#endif /*TA_ATTESTATION_DEFINES_H*/
