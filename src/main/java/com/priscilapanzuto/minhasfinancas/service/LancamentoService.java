package com.priscilapanzuto.minhasfinancas.service;

import java.util.List;

import com.priscilapanzuto.minhasfinancas.model.entity.Lancamento;
import com.priscilapanzuto.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService{
	
	public Lancamento salvar(Lancamento lancamento);
	
	public Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento);
	
	void validar(Lancamento lancamento);

}
