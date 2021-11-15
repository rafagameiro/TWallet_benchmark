/*
 *
 * Thesis: TWallet - ARM TrustZone Enabled Trustable Mobile Wallet: A Case for Cryptocurrency Wallets
 * NOVA School of Science and Technology, NOVA University of Lisbon
 *
 * Student: Rafael Rodrigues Gameiro nº50677
 * Advisor: Henrique João Domingues
 *
 */
#ifndef TA_BENCHMARK_H
#define TA_BENCHMARK_H

/*
 * This UUID is generated with uuidgen
 * the ITU-T UUID generator at http://www.itu.int/ITU-T/asn1/uuid.html
 */
#define TA_BENCHMARK_UUID \
	{ 0x9584c8c8, 0xf3da, 0x439d, \
		{ 0x80, 0x53, 0x11, 0x2c, 0x91, 0x8c, 0xfb, 0x0a} }

/* Additional Constants used in TA functions*/
#define OUTPUT_MAX_LENGTH              1000 

/* The function IDs implemented in this TA */

/*
* B_KEY_GENERATION - Tests Key Generation of different algorithms and sizes
* param[0] (value) number of repetitions the function will call the operation 
* param[1] (value) type of key to generate, and key length
* param[2] (memref) set with the registered values during the test 
* param[3] unused
*/
#define B_KEY_GENERATION   	                        0

/*
* B_STORE_CREDENTIALS - Tests Store Credentials
* param[0] (memref) ID used to identify the program that requested the store operation
* param[1] (memref) Credentials stored
* param[2] unused
* param[3] unused
*/
#define B_STORE_CREDENTIALS               		1

/*
* B_LOAD_CREDENTIALS - Tests Load Credentials Operation
* param[0] (memref) ID used to identify the program that requested the store operation
* param[1] (memref) Credentials stored
* param[2] unused
* param[3] unused
*/
#define B_LOAD_CREDENTIALS		                2

/*
* B_DELETE_CREDENTIALS - Tests Delete Credentials Operation
* param[0] (memref) ID used to identify the program that requested the store operation
* param[1] unused
* param[2] unused
* param[3] unused
*/
#define B_DELETE_CREDENTIALS           		        3
/*
 * B_STORAGE_READ_DATA - Tests Storage Read Data Operation
 * param[0] (memref) ID used the identify the persistent object
 * param[1] (memref) Content the persistent object
 * param[2] unused
 * param[3] unused
 */
#define B_STORAGE_READ_DATA		                4

/*
 * B_STORAGE_WRITE_DATA - Tests Storage Write Data Operation
 * param[0] (memref) ID used the identify the persistent object
 * param[1] (memref) Data to be writen in the persistent object
 * param[2] unused
 * param[3] unused
 */
#define B_STORAGE_WRITE_DATA		                5

/*
 * B_STORAGE_DELETE_DATA - Tests Storage Delete Data Operation
 * param[0] (memref) ID used the identify the persistent object
 * param[1] unused
 * param[2] unused
 * param[3] unused
 */
#define B_STORAGE_DELETE_DATA          		        6

/*
* B_LOGGING_NEW_ENTRY - Tests Logging New Entry Addition Operation
* param[0] (value) number of repetitions the function will call the operation 
* param[1] (memref) set with the registered values during the test 
* param[2] unused
* param[3] unused
*/
#define B_LOGGING_NEW_ENTRY		                7

/*
* B_MONITORING_FILTER - Tests Monitoring Filtering Operation
* param[0] (value) number of repetitions the function will call the operation 
* param[1] (memref) set with the registered values during the test 
* param[2] unused
* param[3] unused
*/
#define B_MONITORING_FILTER   	                        8

#endif /*TA_BENCHMARK_H*/
