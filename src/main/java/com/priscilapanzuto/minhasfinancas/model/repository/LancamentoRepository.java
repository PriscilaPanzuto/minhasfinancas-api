package com.priscilapanzuto.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.priscilapanzuto.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
