package com.priscilapanzuto.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.priscilapanzuto.minhasfinancas.exception.ErroAutenticacao;
import com.priscilapanzuto.minhasfinancas.exception.RegraNegocioException;
import com.priscilapanzuto.minhasfinancas.model.entity.Usuario;
import com.priscilapanzuto.minhasfinancas.model.repository.UsuarioRepository;
import com.priscilapanzuto.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{
	
	private UsuarioRepository repository;

	public UsuarioServiceImpl(UsuarioRepository repository) {
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário nao encontrado para o email informado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse email");
		}
	}

	@Override
	public Optional<Usuario> buscaPorId(Long usuario) {
		return repository.findById(usuario);
	}

}
