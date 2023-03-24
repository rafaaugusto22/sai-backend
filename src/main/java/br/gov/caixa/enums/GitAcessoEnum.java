package br.gov.caixa.enums;

public enum GitAcessoEnum {

	NO(0),
	GUEST(10),
	REPORTER(20),
	DEVELOPER(30),
	MANTEINER(40),
	OWNER(50);

	GitAcessoEnum(Integer codigo) {
		this.setCodigo(codigo);
		
	}
	
	private Integer codigo;
	

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

}
