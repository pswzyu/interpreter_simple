
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>

#include "../../include/qisr.h"
#include "../../include/msp_cmn.h"
#include "../../include/msp_errors.h"


#define  MAX_KEYWORD_LEN 4096

int upload_user_vocabulary()
{
	int ret = -1;
	char UserData[MAX_KEYWORD_LEN];
	unsigned int len = 0;
	FILE *fp = NULL;
	memset(UserData, 0, MAX_KEYWORD_LEN);
	fp = fopen("userwords.txt", "rb");//用户词表，必须为json格式
	if (fp == NULL)
	{
		printf("userwords file cannot open\n");
		return ret;
	}
	len = (unsigned int)fread(UserData, 1, MAX_KEYWORD_LEN, fp);
	UserData[len] = 0;
	fclose(fp);
	MSPUploadData("userwords", UserData, len, "dtt=userword,sub=uup", &ret);
	if(ret != MSP_SUCCESS)
	{
		printf("QISRUploadData with errorCode: %d \n", ret);
	       return ret;
	}
	else
        printf("\n**********QISRUploadData SUCCESS*************\n");
    return ret;
}

void run_iat(const char* src_wav_filename ,  const char* param)
{
	char rec_result[1024*4] = {0};
	const char *sessionID;
	FILE *f_pcm = NULL;
	char *pPCM = NULL;
	int lastAudio = 0 ;
	int audStat = 2 ;
	int epStatus = 0;
	int recStatus = 0 ;
	long pcmCount = 0;
	long pcmSize = 0;
	int ret = 0 ;

	sessionID = QISRSessionBegin(NULL, param, &ret);
    if (ret !=0)
	{
		printf("QISRSessionBegin Failed,ret=%d\n",ret);
	}

	f_pcm = fopen(src_wav_filename, "rb");
	if (NULL != f_pcm) {
		fseek(f_pcm, 0, SEEK_END);
		pcmSize = ftell(f_pcm);
		fseek(f_pcm, 0, SEEK_SET);
		pPCM = (char *)malloc(pcmSize);
		fread((void *)pPCM, pcmSize, 1, f_pcm);
		fclose(f_pcm);
		f_pcm = NULL;
	}
	while (1) {
		unsigned int len = 6400;
		if (pcmSize < 12800) {
			len = pcmSize;
			lastAudio = 1;
		}
		audStat = 2;
		if (pcmCount == 0)
			audStat = 1;
		if (0) {
			if (audStat == 1)
				audStat = 5;
			else
				audStat = 4;
		}
		if (len<=0)
		{
			break;
		}
		printf("\ncsid=%s,count=%d,aus=%d,",sessionID,pcmCount/len,audStat);
		ret = QISRAudioWrite(sessionID, (const void *)&pPCM[pcmCount], len, audStat, &epStatus, &recStatus);
		printf("eps=%d,rss=%d,ret=%d",epStatus,recStatus,ret);
		if (ret != 0)
			break;
		pcmCount += (long)len;
		pcmSize -= (long)len;
		if (recStatus == 0) {
			const char *rslt = QISRGetResult(sessionID, &recStatus, 0, &ret);
			if (ret != MSP_SUCCESS)
			{
				printf("QISRGetResult Failed,ret=%d\n",ret);
				break;
			}
			if (NULL != rslt)
				printf("%s\n", rslt);
		}
		if (epStatus == MSP_EP_AFTER_SPEECH)
			break;
		usleep(150000);
	}
	ret = QISRAudioWrite(sessionID, (const void *)NULL, 0, 4, &epStatus, &recStatus);
	if (ret !=0)
	{
		printf("QISRAudioWrite Failed,ret=%d\n",ret);
	}
	free(pPCM);
	pPCM = NULL;
	while (recStatus != 5 && ret == 0) {
		const char *rslt = QISRGetResult(sessionID, &recStatus, 0, &ret);
		if (NULL != rslt)
		{
			strcat(rec_result,rslt);
		}
		usleep(150000);
	}
	ret=QISRSessionEnd(sessionID, NULL);
	if(ret !=MSP_SUCCESS)
	{
		printf("QISRSessionEnd Failed, ret=%d\n",ret);
	}
	printf("\n=============================================================\n");
	printf("The result is: %s\n",rec_result);
	printf("=============================================================\n");
	usleep(100000);
}

int main(int argc, char* argv[])
{
	///APPID请勿随意改动
	const char* login_configs = "appid = 5435cbf7,dvc=dshaksghs1232143223,  work_dir =   .  ";//用户词表需要用到dvc，若libmsc有权限获取dvc，则无需手动传，否则手动上传。
	const char* param = "sub=iat,ssm=1,auf=audio/L16;rate=16000,aue=speex-wb,ent=sms16k,rst=plain,rse=utf8";//转写为json格式，编码只能为utf8,因Linux系统编码问题，为正确显示结果，设rst=plain，rse=utf8，16k音频aue=speex-wb，8k音频识别aue=speex，
	const char* output_file = "iat_result.txt";
	int ret = 0;
	char key = 0;

	//用户登录
	ret = MSPLogin(NULL, NULL, login_configs);
	if ( ret != MSP_SUCCESS )
	{
		printf("MSPLogin failed , Error code %d.\n",ret);
	}
	//上传热词
	upload_user_vocabulary();
	//开始一路转写会话
       run_iat("wav/iflytek10.wav" ,  param);//iflytek10音频内容为“阳光很好”,识别结果为：“杨光很好”。
	//退出登录
	MSPLogout();
	return 0;
}

