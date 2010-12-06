/*
 * Copyright (C) 2010 Prasanta Paul, http://prasanta-paul.blogspot.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paw.ui;

/**
 * Class holds Status Info.
 * @author Prasanta Paul
 *
 */
public class Status {

	public static boolean isAvailable = true;
	public static boolean isSMSAllowed = true;
	public static String msg = "[Available]";
	public static int max_sms_allowed = 10;
	public static int count_of_sms_sent = 0;
	
	//Shared Preference variables
	public static String SPREF_IS_AVAILABLE = "is_available";
	public static String SPREF_IS_SMS_ALLOWED = "is_sms_allowed";
	public static String SPREF_STATUS_MSG = "status_msg";
	public static String SPREF_MAX_SMS_COUNT = "max_sms_count";
	public static String SPREF_SMS_SENT_COUNT = "sms_sent_count";
}
