
package com.hermix;

/**
 * Clasa care contine Constantele Statice din API.
 * Mai bine trebuia facuta o interfata care sa fie implementat de clasele
 * care ar fiavut nevoie de aceste constante.
 */

public class SC {
	//Mesaje de eroare
	public static final int ERR_Connection_Error = 100;
	public static final int ERR_Not_Joined = 101;
	public static final int ERR_Allready_Connected = 102;
	public static final int ERR_User_Not_Found = 103;
	public static final int ERR_No_Right = 104;
	public static final int ERR_WB_Locked = 105;
	public static final int ERR_Fatal_Error = 106;
	public static final int ERR_Wrong_Parameters = 107;
	public static final int ERR_Non_Fatal_Error = 108;

	public static final int ERR_Unable_To_Change_Nick = 200;

	//mesaje de tip sistem
	public static final int SYS_Connection_Ok = 1;  //
	public static final int SYS_My_Id_Is = 2;  //
	public static final int SYS_Nick_Changed = 3;  //
	public static final int SYS_Identification_Ok = 4;  //
	public static final int SYS_Get_My_Groups = 5;  //
	public static final int SYS_Your_Groups = 6;  //
	public static final int SYS_Get_Connected_Users = 7;  //
	public static final int SYS_Connected_Users = 8;  //
	public static final int SYS_Get_Users_From_Group = 9;  //
	public static final int SYS_Users_From_Group = 10; //
	public static final int SYS_Get_User_Groups = 11; //
	public static final int SYS_User_Groups = 12; //
	public static final int SYS_Client_Options = 13;
	public static final int SYS_Change_Nick = 14;

	public static final int SYS_Join_Group = 100;    //
	public static final int SYS_Join_Group_Ok = 101;    //
	public static final int SYS_Leave_Group = 102;    //
	public static final int SYS_Leave_Group_Ok = 103;    //
	public static final int SYS_User_Join = 104;    //
	public static final int SYS_User_Part = 105;    //
	//public static final int SYS_Send_Message              =   200;
	public static final int SYS_Wake_Up = 201;    //
	public static final int SYS_Create_Group = 300;    //
	public static final int SYS_Create_Group_Ok = 301;    //
	public static final int SYS_Remove_Group = 302;    //

	public static final int SYS_Add_Video_User = 304;
	public static final int SYS_Del_Video_User = 305;
	public static final int SYS_Send_File = 306;    //
	public static final int SYS_Send_File_Ok = 307;    //

	public static final int SYS_Get_File_Ok = 309;    //
	public static final int SYS_Get_History = 310;    //
	public static final int SYS_History = 311;  //

	public static final int SYS_Video_Password = 400;
	public static final int SYS_Deny_Video_User = 401;
	public static final int SYS_Set_Max_Frames = 402;

	public static final int SYS_Send_Audio = 410;
	public static final int SYS_Audio_Password = 411;

	public static final int SYS_Get_File_List = 421;
	public static final int SYS_Get_File_Info = 422;
	public static final int SYS_Get_File = 423;    //

	public static final int SYS_Kick_User = 500;
	public static final int SYS_Get_Ban_List = 501;
	public static final int SYS_Ban_Address = 502;
	public static final int SYS_Unban_Address = 503;

	/**
	 * mesaje de tip utilizator care sunt trimise tuturor
	 * utilizatorilor conectati la serverul de conferencing.
	 * Identificatorul lor este incepind de la 1000, inclusiv.
	 */

	//mesaje de tip grup
	public static final int GRP_Text_Message = 1;
	public static final int GRP_Have_Image_File = 2;
	public static final int GRP_Have_SMV_Data = 3;
	public static final int GRP_User_Join = 4;
	public static final int GRP_User_Leave = 5;

	/*
     * mesaje de tip utilizator care sunt trimise tuturor
     * utilizatorilor care se afla conectati la grupul de
     * discutii. Identificatorul lor este in plaja 50-99
     * inclusiv.
     */

	//mesaje de comunicare cu ESS
	public static final int GRP_ESSCommand = 64;
	public static final int GRP_ESSStatus = 65;
	//added by Qurtach for IntelliX
	public static final int GRP_Text_Share_Command = 85;


	//mesaje ce tin de grafica pe grup
	public static final int GRP_Clear = 100;
	public static final int GRP_Line = 101;
	public static final int GRP_Circle = 102;
	public static final int GRP_Rectangle = 103;
	public static final int GRP_GText = 104;
	public static final int GRP_Free = 105;
	public static final int GRP_Lock_Screen = 106;
	public static final int GRP_Lock_Screen_Ok = 107;
	public static final int GRP_Unlock_Screen = 108;
	public static final int GRP_Unlock_Screen_Ok = 109;
	//next  added by Qurtach:
	public static final int GRP_Background = 110;
	public static final int GRP_Image = 111;
	public static final int GRP_Arc = 112;
	//next  also added by Qurtach:
	public static final int GRP_Delete_Figure = 120;
	public static final int GRP_Transform_All = 121;
	public static final int GRP_TMove_Figure = 122;
	public static final int GRP_TScale_Figure = 123;
	public static final int GRP_2Front_Figure = 124;
	public static final int GRP_2Back_Figure = 125;
	//ultimul mesaj grafic care vine pe grup
	public static final int GRP_Last_Grp_Message = 199;

	//mesaje ce tin de partea audio pe grup
	public static final int GRP_Audio_On = 200;
	public static final int GRP_Audio_Off = 201;
	public static final int GRP_Audio_On_Ok = 202;
	public static final int GRP_Audio_Off_Ok = 203;

	public static final int GRP_Video_On = 210;
	public static final int GRP_Video_Off = 211;
	public static final int GRP_Video_On_Ok = 212;
	public static final int GRP_Video_Off_Ok = 213;

	/**
	 * mesaje grafice definite de utilizator, pentru desenare.
	 * Serverul nu verifica decit utilizatorul, si le paseaza
	 * tuturor clientilor care au cerut whiteboard, de pe grup.
	 * Identificatorul acestor mesaje este incepind de la 1000
	 * inclusiv.
	 */
	//public static final int GRP_USER_DEFINED_1    = 1000;

	//mesaj trimis de un 'adminstrator' care scoate afara un user:
	public static final int SYS_Remote_Disconnect = 1023;

	//added by seKurea
	// the old SYS_Del_Video_User nu trece de server ......
	public static final int SYS_Alt_Del_Video_User = 5000;
	public static final int SYS_Video_Watching = 5050;

	//added by Qurtach:
	public static final boolean WATCH_ON = true;
	public static final boolean WATCH_OFF = false;


}