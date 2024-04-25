package OneWayDev.tn.OneWayDev.Service;


import OneWayDev.tn.OneWayDev.Entity.ModelSTT;
import OneWayDev.tn.OneWayDev.Entity.User;
import OneWayDev.tn.OneWayDev.Repository.ModelsSTTRepository;
import OneWayDev.tn.OneWayDev.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ModelSTTService {

    @Autowired
    private ModelsSTTRepository modelSTTRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ModelSTT addModelSTT(ModelSTT modelSTT) {
        return modelSTTRepository.save(modelSTT);
    }

    @Transactional
    public ModelSTT modifyModelSTT(ModelSTT modelSTT) throws Exception {
        Optional<ModelSTT> existingModelSTT = modelSTTRepository.findById(modelSTT.getId());
        if (existingModelSTT.isPresent()) {
            return modelSTTRepository.save(modelSTT);
        } else {
            throw new Exception("ModelSTT not found");
        }
    }

    @Transactional
    public void deleteModelSTT(Long id) throws Exception {
        Optional<ModelSTT> existingModelSTT = modelSTTRepository.findById(id);
        if (existingModelSTT.isPresent()) {
            modelSTTRepository.delete(existingModelSTT.get());
        } else {
            throw new Exception("ModelSTT not found");
        }
    }

    @Transactional
    public ModelSTT assignUserToModelSTT(Long userId, Long modelSTTId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        Optional<ModelSTT> modelSTT = modelSTTRepository.findById(modelSTTId);

        if (user.isPresent() && modelSTT.isPresent()) {
            modelSTT.get().getUsers().add(user.get());
            return modelSTTRepository.save(modelSTT.get());
        } else {
            throw new Exception("User or ModelSTT not found");
        }
    }


    public List<ModelSTT> getAllModels() {
        return modelSTTRepository.findAll();
    }
}