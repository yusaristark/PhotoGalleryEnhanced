package com.github.yusaristark.photogalleryenhanced

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

       /* Менеджер фрагментов автоматически создает и добавляет размещаемые фрагменты обратно в activity после изменения конфигурации
        или после завершения процесса системой. Добавление фрагмента в контейнер необходимо только в том случае, если фрагмента там еще нет.
        если пакет равен null, то это новый запуск activity, и можно смело предположить, что ни один фрагмент еще не был автоматически восстановлен и переустановлен.
        Если пакет не null, это означает, что activity восстанавливается после уничтожения системы (например, после поворота или уничтожения процесса),
        и все фрагменты, которые были размещены до уничтожения, были восстановлены и добавлены обратно в соответствующие контейнеры.
        */
        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance()).commit()
        }
    }
}