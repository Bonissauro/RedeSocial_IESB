package br.com.bonysoft.redesocial_iesb.realm.repositorio;

/**
 * Created by carlospanarello on 02/10/16.
 */

public enum MensagemLogin {

    SENHA_INVALIDA("Senha Invalida!"),
    JA_EXISTE("Ja existe um usuario registrado nesse aplicativo!"),
    INFORME_SENHA("Favor informe uma senha!"),
    INFORME_EMAIL("Favor informe um email!"),
    USOU_FACE("Voce usou o Facebook como login!"),
    CADASTRAR("Realizar Cadastro"),
    LOGIN_COM_SUCESSO("Login realizado com sucesso!"),
    CADASTRAR_COM_SUCESSO("Cadastro realizado com sucesso!"),
    INFORME_EMAIL_SENHA("Favor informe um email e uma senha!");

    MensagemLogin(String msg) {
        this.msg = msg;
    }

    private String msg;

    public String getMsg() {
        return msg;
    }
}
