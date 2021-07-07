import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;

public class Telegram extends RouteBuilder {
  @Override
  public void configure() throws Exception {
    from("telegram:bots?authorizationToken={{telegram.id}}")
    .log("command received from Bot: ${body}")
    .convertBodyTo(String.class)
    .choice()
    .when(simple("${body} starts with '/cnpj'"))
      .log("Consulta a dados do CNPJ")
      .setHeader(Exchange.HTTP_METHOD, constant("GET"))
      .setHeader(Exchange.HTTP_PATH)
      .groovy("request.body.substring(6,request.body.length())")  
    .to("http://www.receitaws.com.br/v1/cnpj/")
      .unmarshal().json(JsonLibrary.Jackson)
      .transform(simple("Nome Empresa: ${body[nome]}\n CPNJ: ${body[cnpj]}\n Situacao: ${body[situacao]}\n Capital Social: ${body[capital_social]}\n Endereco: ${body[logradouro]}, ${body[numero]} \n Bairro: ${body[bairro]} \n Municipio: ${body[municipio]}\n CEP: ${body[cep]}\n"))
    .to("telegram:bots?authorizationToken={{telegram.id}}")
      .setBody().simple("Posso confirmar atendimento para essa empresa?")
    .to("telegram:bots?authorizationToken={{telegram.id}}")
    .otherwise()
      .setBody().simple("Seja bem vindo a Sefaz-RJ! \n\n Para seguirmos no atendimento vou precisar que voce me responda com algumas opcoes: \n\n\n Se o atendimento e para pessoa Fisica digite: /cpf 99999999999\n\n\n Se o atendimento e para pessoa Juridica digite: /cnpj 999999999999999\n")
    .to("telegram:bots?authorizationToken={{telegram.id}}");
  }
}