#include "git.h"

// 软件定时器设定
static Timer task1_id;
static Timer task2_id;
static Timer task3_id;

// 获取全局变量
const char *topics[] = {S_TOPIC_NAME};

extern U8 bTimeout25ms; /** < 25毫秒定时器超时标志 */

// 硬件初始化
void Hardware_Init(void)
{
    NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2); // 设置中断优先级分组为组2：2位抢占优先级，2位响应优先级
    HZ = GB16_NUM();                                // 字数
    delay_init();                                   // 延时函数初始化
    GENERAL_TIM_Init(TIM_4, 0, 1);
    Usart1_Init(9600); 				// 串口1初始化为9600
		Usart2_Init(115200, 2, 2);// 人脸
		USART4_Init(57600); 		  // 指纹
	
     
	
#if OLED // OLED文件存在
    OLED_Init();
    OLED_ColorTurn(0);   // 0正常显示，1 反色显示
    OLED_DisplayTurn(0); // 0正常显示 1 屏幕翻转显示
#endif
		
  
#if OLED // OLED文件存在
    OLED_Clear();
#endif
}
// 网络初始化
void Net_Init()
{

		u8 i;
		char str[50];
#if OLED // OLED文件存在
    OLED_Clear();
    // 写OLED内容
    sprintf(str, "-蓝牙正在初始化");
    OLED_ShowCH(0, 0, (unsigned char *)str);
		sprintf(str, "-请稍等 ...   ");
    OLED_ShowCH(0, 2, (unsigned char *)str);
#endif
	  Usart3_Init(9600); 		// 串口3，驱动蓝牙

    for ( i=0; i < 1; i++) {
			 Usart_SendString(USART3,"AT+NAMEDEVICE");
        delay_ms(500);
    }
    for (i=0; i < 1; i++) {
				Usart_SendString(USART3,"AT+NAMEDEVICE");
        delay_ms(500);
    }
		  for (i=0; i < 1; i++) {
				Usart_SendString(USART3,"AT+NAMEDEVICE");
        delay_ms(500);
    }
#if OLED              // OLED文件存在
    OLED_Clear();
#endif
}
// 任务1
void task1(void)
{
		
   	Automation_Close();

	
	   
}
// 任务2
void task2(void)
{

    Read_Data(&Data_init);   // 更新传感器数据
		Update_oled_massage();   // 更新OLED
    Update_device_massage(); // 更新设备
    State = ~State;
}
// 任务3
void task3(void)
{
    // 10s发一次
    if (Connect_Net && Data_init.App == 0) {
        Data_init.App = 1;
    }

		//JR6001_SongControl(7,1);
}
// 软件初始化
void SoftWare_Init(void)
{
    // 定时器初始化
    timer_init(&task1_id, task1, 1000, 1); // 每1s上传一次设备数据
    timer_init(&task2_id, task2, 300, 1);    // 跟新数据包
    timer_init(&task3_id, task3, 30000, 1);  // 每30秒发送一次数据到客户端

    timer_start(&task1_id);
    timer_start(&task2_id);
    timer_start(&task3_id);
	
	
}
// 主函数
int main(void)
{

    unsigned char *dataPtr = NULL;
    SoftWare_Init(); // 软件初始化
    Hardware_Init(); // 硬件初始化
    // 启动提示
    Beep_time(100);
    Net_Init();            // 网络初始
    TIM_Cmd(TIM4, ENABLE); // 使能计数器
		
		OLED_Clear();
    OLED_ShowCH(16, 0, "KA键密码验证");
    OLED_ShowCH(16, 2, "KB键人脸验证");
		OLED_ShowCH(16, 4, "KC键指纹验证");
		OLED_ShowCH(16, 6, "KD键RFID验证");
    while (1) {

        // 线程
        timer_loop(); // 定时器执行
        // 串口接收判断
        dataPtr = Hc05_GetIPD(0);
 }
}
