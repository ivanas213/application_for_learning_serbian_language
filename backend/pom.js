const { Question } = require('./models/question'); // Importujte model Question
const connectDB = require('./config/db').connectDB;
const {initializeData}=require('./config/initData')
const express = require('express');
const PORT = process.env.PORT;
const { Lesson } = require('./models/lesson'); // Importujte model Question

const app = express();

// Funkcija za dodavanje topic polja svim pitanjima koja ga nemaju
connectDB().then(() => {
    initializeData()
    app.listen(PORT, '0.0.0.0', () => {
        console.log(`Server slusa na portu ${PORT}...`);
    });
}).catch((error) => {
    console.error('Greška pri pokretanju servera:', error);
});
async function removeStyleFromTextBlocks() {
    try {
      const result = await Lesson.updateMany(
        { "contentBlocks.textBlock": { $exists: true } }, // Filtrira dokumente koji imaju textBlock
        { $unset: { "contentBlocks.$[].textBlock.style": "" } } // Uklanja polje style iz svih textBlockova
      );
      console.log(`Broj ažuriranih dokumenata: ${result.modifiedCount}`);
    } catch (error) {
      console.error('Greška prilikom ažuriranja:', error);
    }
  }
  removeStyleFromTextBlocks();
