package com.alura.pix.consumidor;

import com.alura.pix.dto.PixDTO;
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
    public void processaPix(PixDTO pixDTO, Acknowledgment acknowledgment) {

        System.out.println("Recebendo pix: " + pixDTO.getIdentifier());

        Pix pix = this.pixRepository.findByIdentifier(pixDTO.getIdentifier());

        Key origem = this.keyRepository.findByChave(pixDTO.getChaveOrigem());

        Key destion = this.keyRepository.findByChave(pixDTO.getChaveDestino());

        if (origem == null || destion == null || pix == null || !pix.getChaveOrigem().equals(pix.getChaveOrigem())
                || !pix.getChaveDestino().equals(pix.getChaveDestino())
        ) {
            pix.setStatus(PixStatus.ERRO);
            throw new KeyNotFoundException();
        } else {
            pix.setStatus(PixStatus.PROCESSADO);
        }

        this.pixRepository.save(pix);

        acknowledgment.acknowledge();
    }

}
