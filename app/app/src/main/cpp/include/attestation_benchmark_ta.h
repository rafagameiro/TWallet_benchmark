/*
 *
 * Thesis: TWallet - ARM TrustZone Enabled Trustable Mobile Wallet: A Case for Cryptocurrency Wallets
 * NOVA School of Science and Technology, NOVA University of Lisbon
 *
 * Student: Rafael Rodrigues Gameiro nº50677
 * Advisor: Henrique João Domingues
 *
 */
#ifndef TA_ATTESTATION_BENCHMARK_H
#define TA_ATTESTATION_BENCHMARK_H

/*
 * This UUID is generated with uuidgen
 * the ITU-T UUID generator at http://www.itu.int/ITU-T/asn1/uuid.html
 */
#define TA_ATTESTATION_BENCHMARK_UUID \
	{ 0xcf48eb3a, 0x7f52, 0x4e45, \
		{ 0x81, 0x42, 0x4d, 0x89, 0x47, 0x28, 0x2e, 0x1a} }


/* Additional Constants used in TA functions*/
#define PUBKEY_EXP_LENGTH                               20
#define EC_PUBLIC_X_LENGTH                              1000
#define EC_PUBLIC_Y_LENGTH                              1000
#define BASE64_MAX_LENGTH                               1000

/* The function IDs implemented in this TA */

/*
* TA_GENERATE_KEYS - Generates and stores the generated keys used for the tests
* param[0] (value) Nonce send from the Normal World
* param[1] (memref) Attestation proof
* param[2] unused
* param[3] unused
*/
#define B_GENERATE_KEYS                       		0

/*
* TA_ATTESTATION_GET_PROOF - Generates and sends back an attestation proof of all secure components
* param[0] (value) Nonce send from the Normal World
* param[1] (memref) Attestation proof
* param[2] unused
* param[3] unused
*/
#define B_ATTESTATION_GET_PROOF               		1

/*
* TA_ATTESTATION_GET_PROOF_EC - Same as TA_ATTESTATION_GET_PROOF, but uses EC
* param[0] (value) Nonce send from the Normal World
* param[1] (memref) Attestation proof
* param[2] unused
* param[3] unused
*/
#define B_ATTESTATION_GET_PROOF_EC               	2

#endif /*TA_ATTESTATION_BENCHMARK_H*/