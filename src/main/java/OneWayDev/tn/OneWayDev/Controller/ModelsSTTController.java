
package OneWayDev.tn.OneWayDev.Controller;

import OneWayDev.tn.OneWayDev.Entity.ModelSTT;
import OneWayDev.tn.OneWayDev.Service.ModelSTTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/modelSTT")
public class ModelsSTTController {

    @Autowired
    private ModelSTTService modelSTTService;

    @PostMapping("/addModelSTT")
    public ResponseEntity<Map<String, String>> addModelSTT(@RequestBody ModelSTT modelSTT) {
        ModelSTT createdModelSTT = modelSTTService.addModelSTT(modelSTT);
        Map<String, String> response = new HashMap<>();
        response.put("message", "ModelSTT created successfully");
        response.put("ModelSTTId", String.valueOf(createdModelSTT.getId()));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/modifyModelSTT")
    public ResponseEntity<Map<String, String>> modifyModelSTT(@RequestBody ModelSTT modelSTT) {
        try {
            ModelSTT updatedModelSTT = modelSTTService.modifyModelSTT(modelSTT);
            Map<String, String> response = new HashMap<>();
            response.put("message", "ModelSTT updated successfully");
            response.put("ModelSTTId", String.valueOf(updatedModelSTT.getId()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/deleteModelSTT")
    public ResponseEntity<Map<String, String>> deleteModelSTT(@PathVariable Long id) {
        try {
            modelSTTService.deleteModelSTT(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "ModelSTT deleted successfully");
            response.put("ModelSTTId", String.valueOf(id));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{userId}/{modelSTTId}")
    public ResponseEntity<Map<String, String>> assignUserToModelSTT(@PathVariable Long userId, @PathVariable Long modelSTTId) {
        try {
            ModelSTT updatedModelSTT = modelSTTService.assignUserToModelSTT(userId, modelSTTId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User assigned to ModelSTT successfully");
            response.put("ModelSTTId", String.valueOf(updatedModelSTT.getId()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ModelSTT>> getAllModels() {
        List<ModelSTT> models = modelSTTService.getAllModels();
        return ResponseEntity.ok(models);
    }

}