/*
 *
 * Thesis: TWallet - ARM TrustZone Enabled Trustable Mobile Wallet: A Case for Cryptocurrency Wallets
 * NOVA School of Science and Technology, NOVA University of Lisbon
 *
 * Student: Rafael Rodrigues Gameiro nº50677
 * Advisor: Henrique João Domingues
 *
 */
#ifndef TA_CONTEXT_SWITCH_BENCHMARK_H
#define TA_CONTEXT_SWITCH_BENCHMARK_H

/*
 * This UUID is generated with uuidgen
 * the ITU-T UUID generator at http://www.itu.int/ITU-T/asn1/uuid.html
 */
#define TA_CONTEXT_SWITCH_UUID \
	{ 0x7a8a8f75, 0x35f3, 0x40fe, \
		{ 0x9e, 0x20, 0xfc, 0x92, 0xfb, 0xf0, 0xf9, 0x7c} }


/* Additional Constants used in TA functions*/
#define KEY_SIZE                        1024

/* The function IDs implemented in this TA */

/*
* B_TEST_CONTEXT_SWITCH - Performs an operations an measure its value
* param[0] (value) 
* param[1] unused
* param[2] unused
* param[3] unused
*/
#define B_TEST_CONTEXT_SWITCH                       		0

#endif /*TA_CONTEXT_SWITCH_BENCHMARK_H*/
