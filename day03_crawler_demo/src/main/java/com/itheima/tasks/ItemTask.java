package com.itheima.tasks;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.domain.Item;
import com.itheima.service.ItemService;
import com.itheima.utils.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 实现数据抓取
 */
@Component
public class ItemTask {

    @Autowired
    private ItemService itemService;

    @Autowired
    private HttpClientUtil httpClientUtil;


    /**
     * 抓取数据
     */
    @Scheduled(fixedDelay = 1000)
    public void process() throws IOException {

        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&cid2=653&cid3=655&s=1&click=0&page=";

        for (int i = 1; i < 10; i+=2) {
            //爬取html
            String html = httpClientUtil.getHtml(url + i);
            //解析数据
            getJsoup(html);
        }


    }

    /**
     * 解析数据 保存html
     * @return
     */
    private void getJsoup(String html) throws IOException {

        Document document = Jsoup.parse(html);

        //获取spu集合

        Elements spuElements = document.select("[class=gl-warp] li.gl-item");

        //遍历spu集合获取sku
        for (Element spuElement : spuElements) {
            Long spuId = Long.parseLong(spuElement.attr("data-spu"));

            // 获取sku的集合
            Elements skuElements = spuElement.select("li.ps-item img");
            for (Element skuElement : skuElements) {
                // 获取skuId
                Long skuId = Long.parseLong(skuElement.attr("data-sku"));


                Item itemTemp = new Item();
                // spuId
                itemTemp.setSpu(spuId);
                // skuId
                itemTemp.setSku(skuId);
                // 判断重复
                List<Item> list = itemService.findAllByid(itemTemp);
                if(!list.isEmpty()){
                    continue;
                }

                // 创建时间
                itemTemp.setCreated(new Date());
                // 更新时间
                itemTemp.setUpdated(new Date());
                // 商品详情地址
                itemTemp.setUrl("https://item.jd.com/"+skuId+".html");
                // 商品标题
                String itemHtml = httpClientUtil.getHtml("https://item.jd.com/" + skuId + ".html");
                Document itemDocument  = Jsoup.parse(itemHtml);
                String text = itemDocument.select("div.sku-name").text();
                itemTemp.setTitle(text);
                // 商品图片
                String imgUrl = "https:" + skuElement.attr("data-lazy-img").replace("/n9/","/n7/");
                String imgPtah = httpClientUtil.getImg(imgUrl);
                itemTemp.setPic(imgPtah);
                // 商品价格 https://p.3.cn/prices/mgets?skuIds=J_4120323
                String priceHtml = httpClientUtil.getHtml("https://p.3.cn/prices/mgets?skuIds=J_" + skuId);
                ObjectMapper objectMapper = new ObjectMapper();
                Double price = objectMapper.readTree(priceHtml).get(0).get("p").asDouble();
                itemTemp.setPrice(price);

                // 保存数据
                itemService.save(itemTemp);

            }
        }

    }
}
