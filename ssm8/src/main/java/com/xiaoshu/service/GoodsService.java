package com.xiaoshu.service;

import java.util.Date;
import java.util.List;

import javax.jms.Destination;
import javax.swing.JEditorPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.GoodsMapper;
import com.xiaoshu.dao.TypeMapper;
import com.xiaoshu.entity.Goods;
import com.xiaoshu.entity.GoodsVo;
import com.xiaoshu.entity.Type;

import redis.clients.jedis.Jedis;

@Service
public class GoodsService {

	@Autowired
	GoodsMapper goodsMapper;
	
	@Autowired
	TypeMapper typeMapper;
	
	@Autowired
	Destination queueTextDestination;
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	public PageInfo<GoodsVo> findPage(GoodsVo goodsVo , Integer pageNum , Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<GoodsVo> list = goodsMapper.findList(goodsVo);
		return new PageInfo<>(list);
		
	}
	
	public List<Type> findAll(){
		return typeMapper.selectAll();
	}
	public Goods findByName(String name){
		Goods g = new Goods();
		g.setName(name);
		return goodsMapper.selectOne(g);
	}
	
	public void addGoods(Goods goods){
		goods.setCreatetime(new Date());
		goodsMapper.insert(goods);
		
		Jedis j = new Jedis("127.0.0.1",6379);
		Goods g = new Goods();
		g.setName(goods.getName());
		Goods goods2 = goodsMapper.selectOne(g);
		j.set(goods2.getId()+"", goods2.getName());
		
		jmsTemplate.convertAndSend(queueTextDestination, JSONObject.toJSONString(goods));
	}
	public void updateGoods(Goods goods){
		goodsMapper.updateByPrimaryKeySelective(goods);
	}
	public void deleteGoods(Integer id){
		goodsMapper.deleteByPrimaryKey(id);
	}
	public List<GoodsVo> findList(GoodsVo goodsVo){
		return goodsMapper.findList(goodsVo);
	}
	public List<GoodsVo> countGoods(){
		return goodsMapper.countGoods();
	}
	public void addC(Type type){
		typeMapper.insert(type);
	}
	public Type findByNamec(String typename){
		Type t  = new Type();
		t.setTypename(typename);
		return typeMapper.selectOne(t);
	}
}
