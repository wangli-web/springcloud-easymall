package cn.tedu.user;

import org.junit.Test;

public class BinTest {
	//01100100 表示16384位二进制中8位
	//定义从左到右 下标 0 1 2 3 4 5 6 7
	//将下标和对应的二进制的值
	// 下标号为0的所属权是:1/0
	@Test
	public void test(){
	byte a=100;	
		for(int i=0;i<8;i++){
			int bit=(a>>7-i)&1;
			System.out.println
			("槽道号"+i+"当前节点所属权:"+(bit==1?true:false));
		}
	}
}
