package com.alura.pix.consumidor;

import com.alura.pix.avro.PixRecord;
import com.alura.pix.dto.PixStatus;
import com.alura.pix.exception.KeyNotFoundException;
import com.alura.pix.model.Key;
import com.alura.pix.model.Pix;
import com.alura.pix.repository.KeyRepository;
import com.alura.pix.repository.PixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class PixValidator {

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private PixRepository pixRepository;

    @KafkaListener(topics = "pix-topic", groupId = "grupo")
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(
                    value = 5000L
            ),
            autoCreateTopics = "true",
            include = Exception.class
    )
    public void processaPix(PixRecord pixDTO, Acknowledgment acknowledgment) {

        System.out.println("Recebendo pix: " + pixDTO.getIdentificador());
        System.out.println(pixDTO);

        Pix pix = this.pixRepository.findByIdentifier(pixDTO.getIdentificador().toString());
        if (pix == null)
            throw new RuntimeException("Pix não encontrado");

        Key origem = this.keyRepository.findByChave(pixDTO.getChaveOrigem().toString());

        Key destion = this.keyRepository.findByChave(pixDTO.getChaveDestino().toString());

        if (destion == null || origem == null)
            throw new KeyNotFoundException();

        if (!pix.getChaveOrigem().equals(pixDTO.getChaveOrigem())
                || !pix.getChaveDestino().equals(pixDTO.getChaveDestino())) {
            pix.setStatus(PixStatus.ERRO);
        } else {
            pix.setStatus(PixStatus.PROCESSADO);
        }

        this.pixRepository.save(pix);

        System.out.println("Pix consumido com sucesso");

        acknowledgment.acknowledge();
    }

}
