package cn.tedu.img.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jt.common.utils.UploadUtil;
import com.jt.common.vo.PicUploadResult;
@Service
public class ImgService {
	@Value("${diskPath}")
	private String path;
	@Value("${urlPath}")
	private String urlPath;
	public PicUploadResult picUpload(MultipartFile pic) {
		
		/*主要思路:将图片存储到本地c盘 返回url地址
		 *1.获取图片名称
		 *2.判断图片合法(后缀.jpg.png.gif.***)
		 *	2.1成功:继续逻辑
		 *	2.2失败:Result error=1 return
		 *3.根据图片的原名称生成一个路径的多级地址字符串
		 *	dir=/upload/1/d/3/e/3/d/3/3/ 原名称不变,对应的目录一个
		 *4.生成磁盘路径 file mkdir
		 *	@Value("${path}")+dir
		 *5.重命名文件 uuid.jpg
		 *6.输出pic中的流数据到磁盘中形成一个图片文件
		 *7.拼接url地址 @Value("${urlPath}")+dir+重命名
		 *8.数据赋值返回
		 */
		//准备一个返回的对象
		PicUploadResult result=new PicUploadResult();
		//文件名称校验
		//拿到原名 **.jpg
		try{
			String oldName = pic.getOriginalFilename();
			//截取后缀名称 ldsflsfljdslfjdsl.jpg .png .gif
			String extName = 
			oldName.substring(oldName.lastIndexOf("."));
			//判断后缀合法
			if(!extName.matches(".(png|jpg|gif)$")){
				result.setError(1);
				return result;
			}
			//使用工具类,生成一个多级路径地址,以upload开始的
			String dir="/"+UploadUtil.
					getUploadPath(oldName, "upload")+"/";
			// /upload/a/2/1//2/f/2//d/2
			//c://upload/a/2/1/2/f/2/d/2
			//创建文件夹,文件夹可能存在也可能不存在
			File _dir=new File(path+dir);
			if(!_dir.exists()){//文件夹不存在时
				_dir.mkdirs();
			}
			//重命名文件
			String fileName=UUID.randomUUID().toString()+extName;
			//lsajdflsajdfljsadlf.jpg
			//将这个名称作为文件存储图片数据
			pic.transferTo(new File(path+dir+fileName));
			//result封装url地址
			String url=urlPath+dir+fileName;
			result.setUrl(url);
			return result;
		}catch(Exception e){
			//出现异常
			e.printStackTrace();
			result.setError(1);
			return result;
		}
	}

}
