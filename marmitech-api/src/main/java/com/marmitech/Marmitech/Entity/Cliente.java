package com.marmitech.Marmitech.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank(message = "nome não pode ser null ou vazio")
    private String nome;

    @NotNull
    @NotBlank(message = "email não pode ser null ou vazio")
    private String email;

    @NotNull
    @NotBlank(message = "telefone não pode ser null ou vazio")
    private String telefone;

    @NotNull
    @NotBlank(message = "cpf|cnpj não pode ser null ou vazio")
    private String cpfCnpj;

    @NotNull
    @NotBlank(message = "endereco não pode ser null ou vazio")
    private String endereco;

    private String dataCadastro;

    // private LocalDate dataCadastro = LocalDate.now();
}