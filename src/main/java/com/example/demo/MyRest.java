package com.example.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
public class MyRest {
	@Autowired
	private MiniMapper mapper;
	
	@GetMapping("/getSawon")
    public ArrayList<SawonVo> getSawon(HttpServletRequest request)
    {
		String depart=request.getParameter("depart"); // s01
		return mapper.getSawon(depart);
    }
	
	@GetMapping("/viewSMemo")
	public MemoVo viewSMemo(HttpServletRequest request) throws Exception
	{
		String id=request.getParameter("id");
		
		MemoVo mvo=mapper.viewSMemo(id);
		mvo.setContent(mvo.getContent().replace("\r\n", "<br>"));
		
		String str=ResourceUtils.getFile("classpath:static/data").toPath().toString();
		File file=new File(str+"/"+mvo.getFname());
		long size=file.length();
		
		String imsi=size+" b";
		
		if(size>1024)
		{	
			size=size/1024;
			imsi=size+" kb";
			
			if(size>1024)
			{
				size=size/1024;
				imsi=size+" mb";
			}
		}
		 
	    mvo.setSize(imsi+"");
		
		return mvo;
	}
	@GetMapping("/viewRMemo")
	public MemoVo viewRMemo(HttpServletRequest request) throws Exception
	{
        String id=request.getParameter("id");
		
		MemoVo mvo=mapper.viewSMemo(id);
		mvo.setContent(mvo.getContent().replace("\r\n", "<br>"));
		
		// 읽은 메시지의 state를 1로 변경
		mapper.setState(id);
		
		String str=ResourceUtils.getFile("classpath:static/data").toPath().toString();
		File file=new File(str+"/"+mvo.getFname());
		long size=file.length();
		
		String imsi=size+" b";
		
		if(size>1024)
		{	
			size=size/1024;
			imsi=size+" kb";
			
			if(size>1024)
			{
				size=size/1024;
				imsi=size+" mb";
			}
		}
		 
	    mvo.setSize(imsi+"");
		return mvo;
	}
	
	@GetMapping("/toWork")
	public CommuteVo toWork(HttpSession session)
	{
		String sabun=session.getAttribute("sabun").toString();
		
		mapper.toWork(sabun);
		
		return mapper.getCommState(sabun, LocalDate.now().toString());
	}
	
	@GetMapping("/toHome")
	public CommuteVo toHome(HttpSession session)
	{
		String sabun=session.getAttribute("sabun").toString();
		mapper.toHome(sabun);
		
		return mapper.getCommState(sabun, LocalDate.now().toString());
	}
	
    @GetMapping("/sawonUpdate")
    public SawonVo sawonUpdate(HttpServletRequest request)
    {
    	String id=request.getParameter("id");
    	SawonVo svo=mapper.sawonUpdate(id);
    	String depart=svo.getSabun().substring(0,3);
    	svo.setDepart(depart);
    	
    	// 년,월,일을 분리하여 전송
    	String[] ymd=svo.getIpsa().split("-");
    	
    	svo.setY( Integer.parseInt(ymd[0]) );
    	svo.setM( Integer.parseInt(ymd[1]) );
    	svo.setD( Integer.parseInt(ymd[2]) );
    	
		return svo;
    }
    
    
}







