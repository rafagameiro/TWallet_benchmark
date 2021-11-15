/*
 * Copyright (c) 2016-2017, Linaro Limited
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
