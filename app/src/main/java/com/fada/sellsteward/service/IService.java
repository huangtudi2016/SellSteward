package com.fada.sellsteward.service;

import java.util.List;

import com.fada.sellsteward.domain.Wares;

public interface IService {
	List<Wares> getNoStockWares();
}
