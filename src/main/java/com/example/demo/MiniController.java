package com.example.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MiniController {
    @Autowired
    private MiniMapper mapper;
    
    @GetMapping("/")
    public String home()
    {
    	return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String login()
    {
    	return "/login";
    }
    
    @PostMapping("/loginOk")
    public String loginOk(SawonVo svo,HttpSession session)
    {
    	Integer level=mapper.loginOk(svo.getSabun(),svo.getPwd());
    	
    	if(level != null)
    	{
    		// 세션변수 생성 첫페이지 이동
            //session.setMaxInactiveInterval(86400);
    		session.setAttribute("sabun", svo.getSabun()); 
    		session.setAttribute("level", level);
    		
    		// 사원의 소속 부서=> 세션변수에 저장
    		switch(svo.getSabun().substring(0,3))       // s01001
    		{
    		   case "s01": session.setAttribute("depart", "총무부"); break;
    		   case "s02": session.setAttribute("depart", "자재부"); break;
    		   case "s03": session.setAttribute("depart", "영업부"); break;
    		   case "s04": session.setAttribute("depart", "감사부"); break;
    		}
    		 
    		return "redirect:/main";
    	}
    	else
    	{
    		return "redirect:/login";
    	}
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
    	session.invalidate();
    	return "redirect:/login";
    }
    
    @GetMapping("/main")
    public String main() 
    {
    	return "/main";
    }
    
    @GetMapping("/list")
    public String list(HttpServletRequest request,Model model)
    {
    	int grp=Integer.parseInt(request.getParameter("grp"));
    	
    	int page=1;
		if(request.getParameter("page")!=null)
			page=Integer.parseInt(request.getParameter("page"));
		
		int index=(page-1)*10;
		
		// list에서 페이지 링크
		int pstart,pend,chong;
		
		pstart=page/10;
		if(page%10==0)
			pstart--;
		
		pstart=pstart*10+1;
		
		pend=pstart+9;
		
		chong=mapper.getChong(grp);
		
		if(pend>chong)
			pend=chong;
		
		ArrayList<BoardVo> blist=mapper.list(index,grp);
		model.addAttribute("blist",blist);
		
		model.addAttribute("page",page);
		model.addAttribute("pstart",pstart);
		model.addAttribute("pend",pend);
		model.addAttribute("chong",chong);
		model.addAttribute("grp",grp);
    	
    	String part="";
    	switch(grp)
    	{
    	    case 0: part="전체"; break;
    	    case 1: part="총무부"; break;
    	    case 2: part="자재부"; break;
    	    case 3: part="영업부"; break;
    	    case 4: part="감사부"; break;
    	}
    	model.addAttribute("part",part);
    	
    	return "/list";
    	
    }
    
    @GetMapping("/write")
    public String write()
    {
    	return "/write";
    }
    @PostMapping("/write")
    public String write(BoardVo bvo,HttpSession session)
    {
    	// 세션에 있는 sabun값을 bvo에 저장
    	bvo.setSabun(session.getAttribute("sabun").toString());
    	String a=bvo.getGrp()+"";
    	String b=session.getAttribute("sabun").toString().substring(2,3);
    	                    // level==100
    	
    	int imsi=(int)session.getAttribute("level");  //  imsi >= 70
    	
    	if(a.equals(b) || session.getAttribute("level").toString().equals("100") || bvo.getGrp()==0 ) // 예외가 존재한다. => level==100  , grp==0
    	{
    		// 쓰기 동작
    		mapper.write(bvo);
    		
    		return "redirect:/list?grp="+bvo.getGrp();
    	}
    	else
    	{
    		// 다시 돌아간다
    		return "redirect:/write";
    	}
    	
    }
    
    @GetMapping("/content") 
    public String content(HttpServletRequest request,Model model)
    {
    	int id=Integer.parseInt(request.getParameter("id"));
    	int page=Integer.parseInt(request.getParameter("page"));
    	int grp=Integer.parseInt(request.getParameter("grp"));
    	
    	BoardVo bvo=mapper.content(id);
    	bvo.setContent( bvo.getContent().replace("\r\n", "<br>") );
    	model.addAttribute("bvo",bvo);
    	model.addAttribute("page",page);
    	model.addAttribute("grp",grp);
    	
    	return "/content";
    	
    }
    
    @GetMapping("/delete")
    public String delete(HttpServletRequest request)
    {
    	int id=Integer.parseInt(request.getParameter("id"));
    	int page=Integer.parseInt(request.getParameter("page"));
    	int grp=Integer.parseInt(request.getParameter("grp"));
    	
    	mapper.delete(id);
    	
    	return "redirect:/list?page="+page+"&grp="+grp;
    }
    
    @GetMapping("/update")
    public String update(HttpServletRequest request,Model model)
    {
    	int id=Integer.parseInt(request.getParameter("id"));
    	int page=Integer.parseInt(request.getParameter("page"));
    	int grp=Integer.parseInt(request.getParameter("grp"));
    	
    	BoardVo bvo=mapper.content(id);
    	model.addAttribute("bvo",bvo);
    	model.addAttribute("page",page);
    	model.addAttribute("grp",grp);
    	
    	return "/update";
    	
    }
    
    @PostMapping("/updateOk")
    public String updateOk(BoardVo bvo,HttpServletRequest request)
    {
    	int page=Integer.parseInt(request.getParameter("page"));
    	
    	mapper.updateOk(bvo);
    	
    	return "redirect:/content?id="+bvo.getId()+"&page="+page+"&grp="+bvo.getGrp();
    }
    
    @GetMapping("/myMemo")
    public String myMemo(HttpSession session, Model model,
    		HttpServletRequest request)
    {
    	if(session.getAttribute("sabun")==null)
    	{
    		return "redirect:/login";
    	}
    	else
    	{
    		String sabun=session.getAttribute("sabun").toString();
    		
    		int spage=1,rpage=1;
    		
    		if(request.getParameter("spage")!=null)
    		{
    			spage=Integer.parseInt(request.getParameter("spage"));
    		}
    		if(request.getParameter("rpage")!=null)
    		{
    			rpage=Integer.parseInt(request.getParameter("rpage"));
    		}
    		
    		// 보낸 메모 페이징 처리
    		int sindex;
    		sindex=(spage-1)*20;
    		
    		// 받은 메모 페이징 처리
    		int rindex;
    		rindex=(rpage-1)*20;
    		
    		// 기존 하나 :  pstart,pend,chong
    		// spstart, spend, schong
    		int spstart,spend,schong;
    		spstart=spage/10;
    		if(spage%10==0)
    			spstart--;
    		
    		spstart=spstart*10+1;
    		spend=spstart+9;
    		
    		schong=mapper.getSchong(sabun);
    		if(spend>schong)
    			spend=schong;    		
    		
    		// rpstart, rpend, rchong
    		int rpstart,rpend,rchong;
    		rpstart=rpage/10;
    		if(rpage%10==0)
    			rpstart--;
    		
    		rpstart=rpstart*10+1;
    		rpend=rpstart+9;
    		
    		rchong=mapper.getRchong(sabun);
    		if(rpend>rchong)
    			rpend=rchong;
    		
    		model.addAttribute("spage",spage);
    		model.addAttribute("spstart",spstart);
    		model.addAttribute("spend",spend);
    		model.addAttribute("schong",schong);
    		
    		model.addAttribute("rpage",rpage);
    		model.addAttribute("rpstart",rpstart);
    		model.addAttribute("rpend",rpend);
    		model.addAttribute("rchong",rchong);
    		
        	// 보낸 메세지
        	ArrayList<MemoVo> mlist1=mapper.sendMemo(sabun,sindex);
        	// 받은 메세지 
        	ArrayList<MemoVo> mlist2=mapper.receiveMemo(sabun,rindex);
        	
        	model.addAttribute("mlist1",mlist1);
        	model.addAttribute("mlist2",mlist2);
        	
        	
        	
        	return "/myMemo";
    	}
    	
    }
    
    @PostMapping("/memoWriteOk")
    public String memoWriteOk(MemoVo mvo, MultipartHttpServletRequest multi,
    		HttpSession session) throws Exception
    {
    	String seSabun=session.getAttribute("sabun").toString();
    	MultipartFile file=multi.getFile("fname2");
    	
    	String fname="";
    	if(!file.isEmpty())
    	{
    		String str=ResourceUtils.getFile("classpath:static/data").toPath().toString();
        	fname=file.getOriginalFilename();
        	
        	Path path=Paths.get(str+"/"+fname);
        	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    	}
     	
    	mvo.setFname(fname);
    	   	
    	mvo.setSeSabun(seSabun);
    	//System.out.println(fname);
    	
    	mapper.memoWriteOk(mvo);
    	
    	return "redirect:/myMemo";
    }
    
   @PostMapping("/receiveDel")
   public String receiveDel(HttpServletRequest request)
   {
	   String[] ids=request.getParameter("ids").split(",");
	   String spage=request.getParameter("spage");
	   String rpage=request.getParameter("rpage");
	   
	   for(int i=0;i<ids.length;i++)
	   {
		   mapper.setDelState(ids[i]);
	   }
	   
	   return "redirect:/myMemo?spage="+spage+"&rpage="+rpage;
   }
   
   @PostMapping("/sendDel")
   public String sendDel(HttpServletRequest request)
   {
	   String[] ids=request.getParameter("ids").split(",");
	   String spage=request.getParameter("spage");
	   String rpage=request.getParameter("rpage");
	   
	   for(int i=0;i<ids.length;i++)
	   {
		   mapper.setDelState(ids[i]);
	   }
	   
	   return "redirect:/myMemo?spage="+spage+"&rpage="+rpage;
   }
   
   @GetMapping("/commute")
   public String commute(HttpSession session,Model model)
   {
	   if(session.getAttribute("sabun")==null)
	   {
		   return "redirect:/login";
	   }
	   else
	   {
		   String sabun=session.getAttribute("sabun").toString();
		   
		   // 출근관련
		   CommuteVo cvo=mapper.getToWork(sabun, LocalDate.now().toString());
		   String state="";
		   String state2="";
		   if(cvo!=null)
		   {
			   switch(cvo.getState())
			   {
			       case 0: state="출근 체크 전입니다"; break;
			       case 1: state="정상 출근 후 근무 중 입니다"; break;
			       case 2: state="지각 후 근무 중 입니다"; break;
			   }
			   
			   switch(cvo.getState())
			   {
			       case 3: state2="정상출퇴근"; break;
		           case 4: state2="지각"; break;
		           case 5: state2="조퇴"; break;
		           case 6: state2="지각&조퇴"; break;
			   }
		   
		   }

		   model.addAttribute("state",state);
		   model.addAttribute("state2",state2);
		   model.addAttribute("cvo",cvo);
		      
		   return "/commute";
	   }
	   
   }
   
   @GetMapping("/down")
   public void down(HttpServletRequest request,HttpServletResponse response)
   {
	   String fname=request.getParameter("fname");
	   
	   try
	   {
		   String str=ResourceUtils.getFile("classpath:static/data").toPath().toString();
		   
		   response.setHeader("Content-Type", "application/octet-stream");
		   String fname2=URLEncoder.encode(fname,"utf-8");
		   response.setHeader("Content-Disposition", "attachment; filename="+fname2);
		   response.setContentType("application/unknown");
		   
		   File file=new File(str+"/"+fname);
		   
		   FileInputStream fis=new FileInputStream(file);
		   BufferedInputStream bis=new BufferedInputStream(fis);
		   BufferedOutputStream bos=new BufferedOutputStream(response.getOutputStream());
		   
		   byte[] b=new byte[1000];
		   
		   int chk;
		   while( (chk=bis.read(b)) != -1)
		   {
			   bos.write(b);
		   }
		   
		   bis.close();
		   bos.close();
		   fis.close();
		   
	   }
	   catch (Exception e) 
	   {
		   e.printStackTrace();
	   }
   }
   
   @GetMapping("/sawonList")
	public String sawonList(HttpServletRequest request,Model model)
	{
		String depart=request.getParameter("depart");
		ArrayList<SawonVo> slist=mapper.sawonList(depart);
		
		model.addAttribute("slist",slist);
		
		String part="";
    	switch(depart)
    	{
    	    case "s01": part="총무부"; break;
    	    case "s02": part="자재부"; break;
    	    case "s03": part="영업부"; break;
    	    case "s04": part="감사부"; break;
    	}
    	model.addAttribute("part",part);
		
    	//model.addAttribute("chong",LocalDate.now().lengthOfMonth());
    	
		return "/sawonList";
	}
   
   @PostMapping("/sawonChugaOk")
   public String sawonChugaOk(SawonVo svo)
   {
	   // 사원번호 생성후 vo에 setter
	   int bunho=mapper.getBunho(svo.getDepart()); // 3
	   
	   String sabun=svo.getDepart()+String.format("%03d", bunho);    // s01003
	   System.out.println(sabun);
	   svo.setSabun(sabun);
	   	
	   mapper.sawonChugaOk(svo);
	   
	   return "redirect:/sawonList?depart="+svo.getDepart();
   }
   
   @GetMapping("/sawonDel")
   public String sawonDel(HttpServletRequest request)
   {
	   String id=request.getParameter("id");
	   String sabun=request.getParameter("sabun");
	   String depart=sabun.substring(0,3);
	   mapper.sawonDel(id);
	   
	   // 사원 삭제후 리스트로 이동 => depart => s01
	   return "redirect:/sawonList?depart="+depart;
   }
   
   @PostMapping("/sawonUpdateOk")
   public String sawonUpdateOk(SawonVo svo)
   {
   	// 부서가 바뀌는지 확인
   	// 1. 바뀌지 않으면 폼에서 가져온 name="sabun"의 값을 재저장
   	// 2. 부서가 바뀌면 새로운 사원코드를 발생시켜야 된다..
   	String imsi=svo.getSabun().substring(0,3);
   	svo.getDepart();
   	
   	if(imsi.equals(svo.getDepart()))
   	{
   		// 부서변경이 없다
   	}
   	else
   	{
   		// 부서변경이 발생
   		// 새로운 사원번호를 발생시킨다..
   		int bunho=mapper.getBunho(svo.getDepart());
   		String sabun=svo.getDepart()+String.format("%03d",bunho);
   		svo.setSabun(sabun);
   	}
   	
   	// SawonVo에 sabun필드를 다시 setter
   	mapper.sawonUpdateOk(svo);
   	
   	return "redirect:/sawonList?depart="+imsi;
   }
}






