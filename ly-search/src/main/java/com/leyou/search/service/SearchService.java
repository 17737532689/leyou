package com.leyou.search.service;

import com.leyou.item.pojo.Spu;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;

public interface SearchService {

    SearchResult<Goods> search(SearchRequest searchRequest);

    Goods buildGoods(Spu spu);
}
