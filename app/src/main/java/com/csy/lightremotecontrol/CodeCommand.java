package com.csy.lightremotecontrol;

public class CodeCommand {
    //编码规则
    //起始码S电平宽度为：9000us低电平+4500us高电平
    public static final int startdown = 9000;
    public static final int startup = 4500;

    //数据码由0，1组成：
    //0的电平宽度为：600us低电平+600us高电平，
    public static final int bit0Down = 600;
    public static final int bit0Up = 600;

    //1的电平宽度为：600us低电平+1600us高电平
    public static final int bit1Down = 600;
    public static final int bit1Up = 1600;


    public static int[] CodeBalcony = new int[36];
    public static int[] codeBedroom = new int[36];
    public static int[] CodeSmallLivingRoom = new int[36];
    public static int[] CodeLivingRoom = new int[36];
    public static int[] CodeLeftLawn = new int[36];
    public static int[] CodeRightLawn = new int[36];
    public static int[] CodeBrighter = new int[36];
    public static int[] CodeDarker = new int[36];

    //命令格式（数组内的数值拼接）
    //起始码+8位数据码+8位校验码+1位结束码（写0）。校验码直接取数据的反码
    //先发送低位
    static {
        FillCode(CodeBalcony, 0x11);
        FillCode(codeBedroom, 0x12);
        FillCode(CodeSmallLivingRoom, 0x13);
        FillCode(CodeLivingRoom, 0x14);
        FillCode(CodeLeftLawn, 0x15);
        FillCode(CodeRightLawn, 0x16);
        FillCode(CodeBrighter, 0x21);
        FillCode(CodeDarker, 0x22);
    }

    private static void FillCode(int[] code, int value) {
        code[0] = startdown;
        code[1] = startup;
        for (int i = 0; i < 8; i++) {
            if ((value & 0x01) == 1) {
                code[2 + i * 2] = bit1Down;
                code[3 + i * 2] = bit1Up;
                code[18 + i * 2] = bit0Down;
                code[19 + i * 2] = bit0Up;
            } else {
                code[2 + i * 2] = bit0Down;
                code[3 + i * 2] = bit0Up;
                code[18 + i * 2] = bit1Down;
                code[19 + i * 2] = bit1Up;
            }
            value >>= 1;
        }
        code[34] = bit0Down;
        code[35] = bit0Up;
    }


}