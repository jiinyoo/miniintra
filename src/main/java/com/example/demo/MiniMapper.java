package com.example.demo;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MiniMapper {
    public Integer loginOk(String sabun,String pwd);
    public ArrayList<BoardVo> list(int index,int grp);
    public void write(BoardVo bvo);
    public int getChong(int grp);
    public BoardVo content(int id);
    public void delete(int id);
    public void updateOk(BoardVo bvo);
    public ArrayList<MemoVo> sendMemo(String sabun,int sindex);
    public ArrayList<MemoVo> receiveMemo(String sabun,int rindex);
    public ArrayList<SawonVo> getSawon(String depart);
    public void memoWriteOk(MemoVo mvo);
    public int getSchong(String sabun);
    public int getRchong(String sabun);
    public MemoVo viewSMemo(String id);
    public void setState(String id);
    public void setDelState(String id);
    public void toWork(String sabun);
    public CommuteVo getCommState(String sabun, String today);
    public CommuteVo getToWork(String sabun, String today);
    public void toHome(String sabun);
    public ArrayList<SawonVo> sawonList(String depart);
    public void sawonChugaOk(SawonVo svo);
    public int getBunho(String depart);
    public void sawonDel(String id);
    public SawonVo sawonUpdate(String id);
    public void sawonUpdateOk(SawonVo svo);
}
