package com.example.taller.ui.gallery;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.taller.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class GalleryFragment extends Fragment {


    //Atributos para mapear los controles visuales
    private EditText etArchivo;
    private Button btDescargar;
    private Button btBuscar;
    private Button btCarga;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        //Mapeo los atributos alos controles visuales
        etArchivo = root.findViewById(R.id.etArchivo);
        btDescargar = root.findViewById(R.id.btDescargar);
        btBuscar = root.findViewById(R.id.btBuscar);
        btCarga = root.findViewById(R.id.btCargar);

        btDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descargar();
            }
        });

        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar();
            }
        });

        btCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargar();
            }
        });

        return root;
    }
    private void cargar(){
        String archivo = etArchivo.getText().toString();
        if (archivo.isEmpty()){
            etArchivo.requestFocus();
            Toast.makeText(getContext(), "Escriba un nombre para el documento", Toast.LENGTH_SHORT).show();
        }else{
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(archivo);
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Se subió", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error subiendo", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void buscar() {
        //Utilizamos un file choose de Android
        etArchivo.setText("");
        Intent intento = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intento.setType("application/pdf");
        startActivityForResult(intento,333);

    }

    private Uri uri; // para "recordar" la ubicacion del archivo.
    //onActivityResult captura el resultado de la busqueda del Intent anterior.
    public void onActivityResult(int requestCode, int resultCode, Intent datos){
        uri = datos.getData();
        Toast.makeText(getContext(), "Archivo Seleccionado", Toast.LENGTH_SHORT).show();
    }

    private void descargar() {
        String archivo = etArchivo.getText().toString();
        if (archivo.isEmpty()){
            etArchivo.requestFocus();
            Toast.makeText(getContext(), "Escriba el nombre del documento que busca", Toast.LENGTH_SHORT).show();
        }else{
            //Referencia a un elemento que esta en el storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(archivo);
            String name = storageReference.getName();
            File localFile = null; //Archivo local temp.

            localFile = new File(String.valueOf(getContext().getFilesDir()),archivo+".pdf");

            //Descargo la imagen de manera asincrona
            File finalLocalFile = localFile;
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String ubicacion = finalLocalFile.getAbsolutePath(); //Ruta del archivo temporal
                    Toast.makeText(getContext(), "Descarga completada: "+ubicacion, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error Descargando...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}