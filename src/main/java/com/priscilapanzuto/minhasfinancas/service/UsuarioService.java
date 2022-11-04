package com.priscilapanzuto.minhasfinancas.service;

import java.util.Optional;

import com.priscilapanzuto.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);

	Optional<Usuario> buscaPorId(Long usuario);

}
