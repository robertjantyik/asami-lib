const getLocalization = function () {
    var localizationobj = {
        // separator of parts of a date (e.g. '/' in 11/05/1955)
        '/': "/",
        // separator of parts of a time (e.g. ':' in 05:44 PM)
        ':': ":",
        // the first day of the week (0 = Sunday, 1 = Monday, etc)
        firstDay: 1,
        days: {
            // full day names
            names: ["Vasárnap", "Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat"],
            // abbreviated day names
            namesAbbr: ["V", "H", "K", "Sze", "Cs", "P", "Szo"],
            // shortest day names
            namesShort: ["V", "H", "K", "Sze", "Cs", "P", "Szo"]
        },
        months: {
            // full month names (13 months for lunar calendards -- 13th month should be "" if not lunar)
            names: ["Január", "Február", "Március", "Április", "Május", "Június", "Július", "Augusztus", "Szeptember", "Október", "November", "December", ""],
            // abbreviated month names
            namesAbbr: ["jan.", "febr.", "márc.", "ápr.", "máj.", "jún.", "júl.", "aug.", "szept.", "okt.", "nov.", "dec.", ""]
        },
        // AM and PM designators in one of these forms:
        // The usual view, and the upper and lower case versions
        //      [standard,lowercase,uppercase]
        // The culture does not use AM or PM (likely all standard date formats use 24 hour time)
        //      null
        AM: ["DE", "de", "DE"],
        PM: ["DU", "du", "DU"],
        eras: [
            // eras in reverse chronological order.
            // name: the name of the era in this culture (e.g. A.D., C.E.)
            // start: when the era starts in ticks (gregorian, gmt), null if it is the earliest supported era.
            // offset: offset in years from gregorian calendar
            { "name": "A.D.", "start": null, "offset": 0 }
        ],
        twoDigitYearMax: 2029,
        patterns: {
            d: "yyyy.MM.dd.",
            D: "yyyy.MMMM d.",
            t: "H:mm",
            T: "H:mm:ss",
            f: "yyyy.MMMM d. H:mm",
            F: "yyyy.MMMM d. H:mm:ss",
            M: "MMMM d.",
            Y: "yyyy.MMMM",
            // S is a sortable format that does not vary by culture
            S: "yyyy\u0027-\u0027MM\u0027-\u0027dd\u0027T\u0027HH\u0027:\u0027mm\u0027:\u0027ss"
	        },
        percentsymbol: "%",
        currencysymbol: "Ft",
        currencysymbolposition: "after",
        decimalseparator: '.',
        thousandsseparator: ',',
        pagergotopagestring: "Oldalra ugrás:",
        pagershowrowsstring: "Sorok mutatása:",
        pagerrangestring: " / ",
        pagerfirstbuttonstring: "Első",
        pagerlastbuttonstring: "Utolsó",
        pagerpreviousbuttonstring: "Előző",
        pagernextbuttonstring: "Következő",
        groupsheaderstring: "Egy adott oszlop szerinti csoportosításhoz fogja a kívánt oszlopot és húzza ide.",
        sortascendingstring: "Növekvő sorrend",
        sortdescendingstring: "Csökkenő sorrend",
        sortremovestring: "Sorrend eltávolítás",
        groupbystring: "Csoportosítás ezen oszlop szerint",
        groupremovestring: "Eltávolítás a csoportosításból",
        filterclearstring: "Mégsem",
        filterstring: "Szűrés",
        filtershowrowstring: "Sorok mutatása, amely:",
        filterorconditionstring: "Vagy",
        filterandconditionstring: "És",
        filterselectallstring: "(Mindet kijelöl)",
        filterchoosestring: "Kérem válasszon:",
        filterstringcomparisonoperators: ['üres', 'nem üres', 'tartalmazza', 'tartalmazza(pontos egyezés)',
            'nem tartalmazza', 'nem tartalmazza(pontos egyezés)', 'ezzel kezdődjön', 'ezzel kezdődjön(pontos egyezés)',
            'ezzel végződjön', 'ezzel végződjön(pontos egyezés)', 'egyenlő', 'egyenlő(pontos egyezés)', 'nulla', 'nem nulla'],
        filternumericcomparisonoperators: ['egyenlő', 'nem egyenlő', 'kisebb mint', 'kisebb vagy egyenlő', 'nagyobb mint', 'nagyobb vagy egyenlő', 'nulla', 'nem nulla'],
        filterdatecomparisonoperators: ['egyenlő', 'nem egyenlő', 'kisebb mint', 'less than or equal', 'greater than', 'greater than or equal', 'null', 'not null'],
        filterbooleancomparisonoperators: ['egyenlő', 'nem egyenlő'],
        validationstring: "A megadott érték nem érvényes",
        emptydatastring: "Nincs megjeleníthető adat",
        filterselectstring: "Válasszon szűrőt",
        loadtext: "Betöltés...",
        clearstring: "Törlés",
        todaystring: "Ma",

        backString: "Vissza",
        forwardString: "Következő",
        toolBarPreviousButtonString: "Vissza",
        toolBarNextButtonString: "Következő",
        emptyDataString: "Nincs adat megjelenítve",
        loadString: "Töltés...",
        clearString: "Törlés",
        todayString: "Ma",
        dayViewString: "Nap",
        weekViewString: "Hét",
        monthViewString: "Hónap",
        agendaViewString: "Események",
        timelineDayViewString: "Idővonal Nap",
        timelineWeekViewString: "Idővonal Hét",
        timelineMonthViewString: "Idővonal Hónap",
        loadingErrorMessage: "A betöltés sikertelen!",
        editRecurringAppointmentDialogTitleString: "Ismétlődő időpont szerkesztése",
        editRecurringAppointmentDialogContentString: "Csak ezt az egyetlen eseményt vagy a sorozatot szeretné szerkeszteni?",
        editRecurringAppointmentDialogOccurrenceString: "Előfordulások szerkesztése",
        editRecurringAppointmentDialogSeriesString: "Sorozat szerkesztése",
        editDialogTitleString: "Időpont szerkesztése",
        editDialogCreateTitleString: "Új időpont hozzáadása",
        contextMenuEditAppointmentString: "Időpont szerkesztése",
        contextMenuCreateAppointmentString: "Új időpont hozzáadása",
        editDialogSubjectString: "Tárgy",
        editDialogLocationString: "Rövid leírás",
        editDialogFromString: "Mettől",
        editDialogToString: "Meddig",
        editDialogAllDayString: "Egész nap?",
        editDialogExceptionsString: "Kivételek",
        editDialogResetExceptionsString: "Előző mentés visszaállítása",
        editDialogDescriptionString: "Részletes leírás",
        editDialogResourceIdString: "Csoport",
        editDialogStatusString: "Státusz",
        editDialogColorString: "Szín",
        editDialogColorPlaceHolderString: "Válasszon színt",
        editDialogTimeZoneString: "Időzóna",
        editDialogSelectTimeZoneString: "Válasszon időzónát",
        editDialogSaveString: "Mentés",
        editDialogDeleteString: "Törlés",
        editDialogCancelString: "Mégsem",
        editDialogRepeatString: "Ismétlés",
        editDialogRepeatEveryString: "Összes ismétlése",
        editDialogRepeatEveryWeekString: "Hét",
        editDialogRepeatEveryYearString: "Év",
        editDialogRepeatEveryDayString: "Nap",
        editDialogRepeatNeverString: "Soha",
        editDialogRepeatDailyString: "Naponta",
        editDialogRepeatWeeklyString: "Hetente",
        editDialogRepeatMonthlyString: "Havonta",
        editDialogRepeatYearlyString: "Évente",
        editDialogRepeatEveryMonthString: "Hónap",
        editDialogRepeatEveryMonthDayString: "Minden nap",
        editDialogRepeatFirstString: "Első",
        editDialogRepeatSecondString: "Második",
        editDialogRepeatThirdString: "Harmadik",
        editDialogRepeatFourthString: "Negyedik",
        editDialogRepeatLastString: "Útolsó",
        editDialogRepeatEndString: "Vége",
        editDialogRepeatAfterString: "Után",
        editDialogRepeatOnString: "Rajta",
        editDialogRepeatOfString: "Tőle",
        editDialogRepeatOccurrencesString: "Előfordulások",
        editDialogRepeatSaveString: "Előfordulások mentése",
        editDialogRepeatSaveSeriesString: "Sorozat mentése",
        editDialogRepeatDeleteString: "Előfordulások törlése",
        editDialogRepeatDeleteSeriesString: "Sorozat törlése",
        editDialogStatuses:
            {
                free: "Szabad",
                tentative: "Kísérleti",
                busy: "Elfoglalt",
                outOfOffice: "Házon kívül"
            }
    };
    return localizationobj;
};